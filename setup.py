import subprocess as sp
import os
import shutil

if __name__=="__main__":
    lib1="lib" + os.sep + "jackson-annotations-2.13.3.jar"
    lib2="lib" + os.sep + "jackson-core-2.13.3.jar"
    lib3="lib" + os.sep + "jackson-databind-2.13.3.jar"
    src="src" + os.sep + "quabla"
    target="src"+ os.sep + "quabla" + os.sep + "QUABLA.java"
    jarfile="Quabla.jar"
    manifest="Quabla.mani"

    print('Hello world!')
    if os.path.exists(jarfile):
        os.remove(jarfile)

    if os.path.exists('quabla'+os.sep):
        shutil.rmtree('quabla'+os.sep)
    # Compile
    sp.run(["javac", "-encoding", "UTF-8", \
            "-cp", ".:"+lib1+":"+lib2+";"+lib3+":"+src, \
            "-sourcepath", "src",\
            "-d", ".", target])
    # Make  jar file and add manifest file
    sp.run(["jar", "cvfm", jarfile, manifest, "quabla"])

    print()
    print('Have a good day!!')