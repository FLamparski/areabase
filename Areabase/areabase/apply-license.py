#!/usr/bin/env python3

import os
import re
import sys

JLICENSE_HEADER_TOP = "/** !license-block \n"
JLICENSE_HEADER_BOTTOM = "*/"
license_text = ""

def get_license_text():
    """returns the license text"""
    global license_text
    if license_text == "":
        with open("LICENSE", "r", encoding="utf-8") as lfile:
            license_lines = lfile.readlines()
            for line in license_lines:
                license_text += line
    return license_text

def writeheader_java(filename, header, skip=None):
    """
    Will process all java files to add the license, and
    preserve the "package" declaration on the first line,
    as it is used to generate the Areabase prettycode page.
    """
    text = []
    with open(filename, "r", encoding="utf-8") as f:
        text = f.readlines()

    output = []

    if len(text) > 0 and text[0].startswith("package"):
        output.append(text[0])
        text = text[1:]

    if skip and skip(text[0]):
        print("Skipping {}.".format(filename))
        return

    output.extend(header)
    for line in text:
        output.append(line)

    with open(filename, "w", encoding="utf-8") as f:
        f.writelines(output)
        print("Done processing {}.".format(filename))

if __name__ == "__main__":
    print(get_license_text())
    javalicense = JLICENSE_HEADER_TOP + get_license_text() + JLICENSE_HEADER_BOTTOM
    for root, dirs, files in os.walk(os.getcwd()):
        for file in files:
            if file.endswith(".java"):
                print("writeheader_java({}, LICENSE)".format(os.path.join(root, file)))
                skip = lambda text: text.startswith(JLICENSE_HEADER_TOP)
                writeheader_java(os.path.join(root, file), javalicense, skip)
