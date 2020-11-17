NOM_FICHIER = "merged5.txt"
DOSSIER_TESTS = "tests/"
NOM_TESTS = "test"

file = open(NOM_FICHIER)
text = []

for line in file.readlines():
    text.append(line)

for i in range(50000, 5050000, 50000):
    new_file = open(DOSSIER_TESTS + NOM_TESTS + str(i), "w")
    for j in range(i):
        new_file.write(text[j % 1000000])
    new_file.close()