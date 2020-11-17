import os
import time
import matplotlib.pyplot as plt
import numpy as np

TEXTE = "56667-0.txt"
REGEX_A_TESTER = ["account", "the", "whithdz", "whitwhi", "amamo", "'a|bc*'", "'wh(at.*|o)'"]

resultats = dict()
for regex in REGEX_A_TESTER:

    # test de notre algo (simple + kmp)
    start_time = time.time()
    os.system('java -cp out/production/workspace/ Main2 ' + regex + ' ' + TEXTE + ' -fv -p3 &> /dev/null')
    ours_kmp_total_time = (time.time() - start_time)

    # test de notre algo (simple + kmp + multi-coeur)
    start_time = time.time()
    os.system('java -cpm out/production/workspace/ Main2 ' + regex + ' ' + TEXTE + ' -fv -p3 &> /dev/null')
    ours_kmp_multi_total_time = (time.time() - start_time)

    # test d'egrep
    start_time = time.time()
    os.system('egrep ' + regex + ' ' + TEXTE + ' &> /dev/null')
    egrep_total_time = (time.time() - start_time)

    resultats[regex] = dict()
    resultats[regex]["ours_kmp"] = ours_kmp_total_time
    resultats[regex]["ours_kmp_multi"] = ours_kmp_multi_total_time
    resultats[regex]["egrep"] = egrep_total_time


ours_kmp = []
for regex, method in resultats.items():
    ours_kmp.append(method["ours_kmp"])

ours_kmp_multi = []
for regex, method in resultats.items():
    ours_kmp_multi.append(method["ours_kmp_multi"])

egrep = []
for regex, method in resultats.items():
    egrep.append(method["egrep"])

# Position sur l'axe des x pour chaque étiquette
position = np.arange(len(REGEX_A_TESTER))
# Largeur des barres
largeur = .20

# Création de la figure et d'un set de sous-graphiques
fig, ax = plt.subplots()
r1 = ax.bar(position - largeur, ours_kmp, largeur)
r2 = ax.bar(position, ours_kmp_multi, largeur)
r3 = ax.bar(position + largeur, egrep, largeur)

# Modification des marques sur l'axe des x et de leurs étiquettes
ax.set_xticks(position)
ax.set_xticklabels(REGEX_A_TESTER)

#plt.show()

print(resultats)


