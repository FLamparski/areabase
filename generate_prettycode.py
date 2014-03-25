#!/usr/bin/env python3

import os
import argparse
import time
from pygments import highlight
from pygments.lexers import get_lexer_by_name
from pygments.formatters import HtmlFormatter

DOCUMENTROOT_BEGIN = """<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Areabase | Code Listings | Filip Wieland (Candidate 0000 @ Centre 00000)</title>

    <!-- Stylesheet (compiled from Sass) -->
    <link href="./stylesheets/screen.css" rel="stylesheet" type="text/css" media="screen, projection" />
    <link href="./stylesheets/print.css" rel="stylesheet" type="text/css" media="print"/>
	
	<link href="./stylesheets/pygments.css" rel="stylesheet" type="text/css" media="screen, projection, print" />

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
  <a href="https://github.com/FLamparski/areabase" class="visible-lg visible-md hidden-print"><img style="position: absolute; top: 0; right: 0; border: 0; z-index: 2000;" src="https://camo.githubusercontent.com/38ef81f8aca64bb9a64448d0d70f1308ef5341ab/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6461726b626c75655f3132313632312e706e67" alt="Fork me on GitHub" data-canonical-src="https://s3.amazonaws.com/github/ribbons/forkme_right_darkblue_121621.png"></a>
    <div class="container">
        <div class="row">
			<div class="jumbotron jumbotron-section">
				<h1>Areabase Code</h1>
				<p>All of the code, pretty printed.</p>
			</div>
		</div>
		
		<div class="row">
		<div class="col-xs-12">
<!-- BEGIN CODE BLOCK -->"""

DOCUMENTROOT_END = """<!-- END CODE BLOCK -->
                </div>
		</div>
		<hr />
		<div class="row">
		<footer class="col-xs-12">
                <a href="http://flamparski.github.io/areabase">Part of Areabase</a> &bull;
                Generated in {gen_time} ms by <code>generate_prettycode.py</code>.
		</footer>
		</div>
	</div>

    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://code.jquery.com/jquery.js"></script>
    <script src="./js/bootstrap.min.js"></script>
  </body>
</html>
"""

JAVA_FILE_BOX = """
<div class="panel panel-default">
<div class="panel-heading"><h3 class="panel-title">{classname} <small>{packagename}</small></h3></div>
<div class="panel-body">{code}</div>
</div>
"""

XML_FILE_BOX = """
<div class="panel panel-default">
<div class="panel-heading"><h3 class="panel-title">{filename}</h3></div>
<div class="panel-body">{code}</div>
</div>
"""

def process_java_file(filename, content, template):
    packagename = content.split("\n")[0][8:-1]
    classname = filename[:-5]
    code = highlight(content, get_lexer_by_name('java'), HtmlFormatter(linenos=False))
    return template.format(classname = classname, packagename=packagename, code = code)

def process_xml_file(filename, content, template):
    code = highlight(content, get_lexer_by_name('xml'), HtmlFormatter(linenos=False))
    return template.format(filename = filename, code = code)

def read_file(path):
    fcontent = ""
    flines = []
    with open(path, "r", encoding="utf-8") as f:
        flines = f.readlines()
    for line in flines:
        fcontent += line
    return fcontent

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("source_path",
                        help="Full path to the /src/main directory", type=str)
    args = parser.parse_args()
    start_time = time.time()
    java_boxes = []
    xml_boxes = []
    for root, dirs, files in os.walk(args.source_path, topdown=True):
        for file in files:
            if file.endswith(".java"):
                java_boxes.append(process_java_file(file,
                                                    read_file(os.path.join(root, file)),
                                                    JAVA_FILE_BOX))
            elif file.endswith(".xml"):
                filename = os.path.join(root, file)
                androidname = filename[len(args.source_path)+1:].replace('\\', '/')
                xml_boxes.append(process_xml_file(androidname,
                                                  read_file(filename),
                                                  XML_FILE_BOX))
    java_boxes_str = ""
    for box in java_boxes:
        java_boxes_str += box
    xml_boxes_str = ""
    for box in xml_boxes:
        xml_boxes_str += box
    codeboxes = java_boxes_str + xml_boxes_str
    gen_time = (time.time() - start_time)*1000
    codefile = DOCUMENTROOT_BEGIN + codeboxes + DOCUMENTROOT_END.format(gen_time=gen_time)
    with open("prettycode.html", "w", encoding="utf-8") as hfile:
        hfile.write(codefile)
