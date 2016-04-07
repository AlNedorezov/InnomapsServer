#!/usr/bin/python

header = """<?xml version="1.0" encoding="UTF-8"?>
<graphml xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://graphml.graphdrawing.org/xmlns" xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd
http://graphml.graphdrawing.org/xmlns
http://graphml.graphdrawing.org/xmlns ">
    <key id="vertex_label" for="node" attr.name="Vertex Label" attr.type="string" />
    <graph edgedefault="undirected">
"""

footer = """
	</graph>
</graphml>"""

def paste_header(file):
	file.write(header)

def paste_footer(file):
	file.write(footer)

def copy_nodes(file_input, file_output):
	line = file_input.readline()
	while line.find("<node") == -1:
		line = file_input.readline()

	file_output.write(line)
	lastpos = file_input.tell()
	line = file_input.readline()
	while line.find("<edge") == -1:
		lastpos = file_input.tell()
		file_output.write(line)
		line = file_input.readline()
	file_input.seek(lastpos)

def copy_edges(file_input, file_output):
	line = file_input.readline()
	while line.find("</graph>") == -1:
		file_output.write(line)
		line = file_input.readline()

input_files_names = ["1.xml", "2.xml", "3.xml", "4.xml", "5.xml"]
input_files_descriptors = []
output_file_name = "9.xml"
output_file_descriptor = None
interfloors_file = "6.xml"
interfloors_file_descriptor = None

def copy_interfloors_edges(file_input, file_output):
	line = file_input.readline()
	while line != "":
		file_output.write(" "*8 + line)
		line = file_input.readline()

for i in range(0, len(input_files_names)):
	input_files_descriptors.append(open(input_files_names[i], "r"))

output_file_descriptor = open(output_file_name, "w")
paste_header(output_file_descriptor)
for i in range(0, len(input_files_descriptors)):
	copy_nodes(input_files_descriptors[i], output_file_descriptor)
for i in range(0, len(input_files_descriptors)):
	copy_edges(input_files_descriptors[i], output_file_descriptor)
output_file_descriptor.write("\n")

for descriptor in input_files_descriptors:
	descriptor.close()

interfloors_file_descriptor = open(interfloors_file)
copy_interfloors_edges(interfloors_file_descriptor, output_file_descriptor)
interfloors_file_descriptor.close()

paste_footer(output_file_descriptor)

output_file_descriptor.close()
