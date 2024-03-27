import shutil
import subprocess

COMMANDS = [
    # Test shred
    "fill-data tmp.txt",
    "shred",  # should not work
    "shred tmp.txt",

    # Test fill-data
    "fill-data tmp.txt",
    "shred tmp.txt",
    "fill-data tmp.txt -S ;",
    "shred tmp.txt",
    # these also should not work at all
    "fill-data",
    "fill-data [path] -S",
    "fill-data -S",
    "fill-data -S [seperator]"

    # testing dump-db
    "shred application-context.json",
    "dump-db",  # should not work, since there are no previous sessions
    "fill-data tmp.txt",
    "dump-db",
    "dump-db tmp.txt",
    "dump-db tmp.txt -S :",
    "dump-db -S",  # will not work, no seperator given
    "shred tmp.txt",
    "shred application-context.json"
]

PREFIX = "java -jar sielayv2.jar "

if __name__ == "__main__":
    for command in COMMANDS:
        subprocess.Popen(PREFIX + command, shell=True).communicate()

    # cleanup
    shutil.rmtree("databases")
