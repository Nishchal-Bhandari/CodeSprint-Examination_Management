import sys
import textwrap

IN = 'PROJECT_DOCUMENTATION.md'
OUT = 'PROJECT_DOCUMENTATION.pdf'

def escape_paren(s):
    return s.replace('\\', '\\\\').replace('(', '\\(').replace(')', '\\)')

with open(IN, 'r', encoding='utf-8') as f:
    md = f.read()

# Very simple markdown -> plain text
lines = []
for raw in md.splitlines():
    s = raw.rstrip()
    if s.startswith('#'):
        # header -> uppercase line
        s2 = s.lstrip('#').strip()
        if s2:
            lines.append(s2.upper())
            lines.append('')
        continue
    # lists
    if s.lstrip().startswith('- '):
        lines.append('  * ' + s.lstrip()[2:])
        continue
    if s.lstrip().startswith('* '):
        lines.append('  * ' + s.lstrip()[2:])
        continue
    # code fences and other markup: just include
    lines.append(s)

# Wrap lines to fit approx 90 characters
wrapped = []
for ln in lines:
    if ln.strip()=='' :
        wrapped.append('')
    else:
        for w in textwrap.wrap(ln, width=90):
            wrapped.append(w)

# PDF layout settings
page_width = 612
page_height = 792
margin_left = 72
margin_top = 72
font_size = 12
leading = 14
max_lines_per_page = int((page_height - 2*margin_top) / leading)

# Build PDF objects
objs = []
contents = []
pages = []
cur_line = 0
page_count = 0
while cur_line < len(wrapped):
    page_count += 1
    slice_lines = wrapped[cur_line:cur_line+max_lines_per_page]
    cur_line += max_lines_per_page
    # build content stream
    y0 = page_height - margin_top - font_size
    # Use text positioning with Td
    content_stream = 'BT\n/F1 %d Tf\n%d %d Td\n' % (font_size, margin_left, int(y0))
    first = True
    for ln in slice_lines:
        esc = escape_paren(ln)
        if first:
            content_stream += '(%s) Tj\n' % esc
            first = False
        else:
            content_stream += '0 -%d Td\n(%s) Tj\n' % (leading, esc)
    content_stream += 'ET\n'
    contents.append(content_stream.encode('utf-8'))

# Start assembling PDF binary
pdf = bytearray()
pdf.extend(b"%PDF-1.4\n%\xE2\xE3\xCF\xD3\n")
obj_offsets = []

# helper to add object
def add_obj(data_bytes):
    obj_offsets.append(len(pdf))
    pdf.extend(data_bytes)

# objects: will create font obj (obj 3), contents obj per page, page objs, pages obj, catalog
# Reserve object numbers: 1..n
# We'll create in order: 1..(2+page_count*2)

# 1: Font
obj_num = 1
obj_offsets_map = {}
obj_offsets_map['font'] = obj_num
add_obj(('%d 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n' % obj_num).encode('utf-8'))

# contents objects start at obj 2
content_obj_nums = []
for i, c in enumerate(contents, start=2):
    content_obj_nums.append(i)
    stream = b"%d 0 obj\n<< /Length %d >>\nstream\n" % (i, len(c))
    add_obj(stream)
    add_obj(c)
    add_obj(b"endstream\nendobj\n")

# page objects follow
page_obj_nums = []
p_start = 2 + len(contents)
for i in range(len(contents)):
    objn = p_start + i
    page_obj_nums.append(objn)
    # placeholder, will reference content obj and font
    # Build page object bytes now
    content_ref = content_obj_nums[i]
    page_obj = ("%d 0 obj\n<< /Type /Page /Parent %d 0 R /MediaBox [0 0 %d %d] /Contents %d 0 R /Resources << /Font << /F1 %d 0 R >> >> >>\nendobj\n" % (objn, 1000000, page_width, page_height, content_ref, 1)).encode('utf-8')
    # Note: we set Parent to a placeholder 1000000, will fix later by replacing
    add_obj(page_obj)

# pages object (we'll assign it obj number next)
pages_obj_num = p_start + len(contents)
# Count and Kids
kids = ''.join(['%d 0 R ' % n for n in page_obj_nums])
pages_obj = ("%d 0 obj\n<< /Type /Pages /Kids [ %s] /Count %d >>\nendobj\n" % (pages_obj_num, kids, len(page_obj_nums))).encode('utf-8')
add_obj(pages_obj)

# catalog object
catalog_obj_num = pages_obj_num + 1
catalog_obj = ("%d 0 obj\n<< /Type /Catalog /Pages %d 0 R >>\nendobj\n" % (catalog_obj_num, pages_obj_num)).encode('utf-8')
add_obj(catalog_obj)

# Now we need to fix the Parent references in page objects. We used placeholder 1000000.
# To simplify, rebuild PDF: easier to assemble objects with known numbers in correct order.
# We'll reconstruct cleanly.

# Reconstruct pdf properly
pdf = bytearray()
pdf.extend(b"%PDF-1.4\n%\xE2\xE3\xCF\xD3\n")
obj_positions = []

# function to write a generic object and record position
def write_obj(n, s):
    obj_positions.append(len(pdf))
    pdf.extend(('%d 0 obj\n' % n).encode('utf-8'))
    pdf.extend(s)
    if not s.endswith(b"\n"):
        pdf.extend(b"\n")
    pdf.extend(b"endobj\n")

# 1: Font
write_obj(1, b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n")

# content objs 2..(1+len(contents))
for idx, c in enumerate(contents, start=2):
    write_obj(idx, b"<< /Length %d >>\nstream\n" % len(c) + c + b"\nendstream\n")

# page objs
page_start = 2 + len(contents)
for i, pnum in enumerate(range(page_start, page_start+len(contents))):
    content_ref = 2 + i
    page_dict = ('<< /Type /Page /Parent %d 0 R /MediaBox [0 0 %d %d] /Contents %d 0 R /Resources << /Font << /F1 %d 0 R >> >> >>\n' % (page_start+len(contents), page_width, page_height, content_ref, 1)).encode('utf-8')
    write_obj(pnum, page_dict)

# pages obj number
pages_num = page_start + len(contents)
kids = b' '.join([('%d 0 R' % n).encode('utf-8') for n in range(page_start, page_start+len(contents))])
write_obj(pages_num, b"<< /Type /Pages /Kids [ " + kids + b" ] /Count %d >>\n" % len(contents))

# catalog
catalog_num = pages_num + 1
write_obj(catalog_num, ('<< /Type /Catalog /Pages %d 0 R >>\n' % pages_num).encode('utf-8'))

# xref
xref_start = len(pdf)
pdf.extend(b"xref\n")
pdf.extend(('0 %d\n' % (catalog_num+1)).encode('utf-8'))
pdf.extend(b"0000000000 65535 f \n")
for pos in obj_positions:
    pdf.extend(('%010d 00000 n \n' % pos).encode('utf-8'))

# trailer
pdf.extend(b"trailer\n")
pdf.extend(('<< /Size %d /Root %d 0 R >>\n' % (catalog_num+1, catalog_num)).encode('utf-8'))
pdf.extend(b"startxref\n")
pdf.extend(('%d\n' % xref_start).encode('utf-8'))
pdf.extend(b"%%EOF\n")

with open(OUT, 'wb') as f:
    f.write(pdf)

print('Wrote', OUT)
