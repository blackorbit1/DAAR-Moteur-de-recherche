import datetime
import os
import time
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import numpy as np
import re


TEXTE = "tests/test50000"
REGEX_A_TESTER = ["account", "the", "whithdz", "whitwhi", "titions", "'a|bc*'", "'wh(at.*|o)'"]
NB_TESTS = 5


resultats = dict()
for regex in REGEX_A_TESTER:

    ours_simple_total_time = 0
    ours_kmp_total_time = 0
    ours_kmp_multi_total_time = 0
    egrep_total_time = 0

    for _ in range(NB_TESTS):
        # test de notre algo (simple)
        start_time = time.time()
        os.system('java -cp out/production/workspace/ Main2_screwed ' + regex + ' ' + TEXTE + ' -f -p3 &> /dev/null')
        tmp = (time.time() - start_time)
        ours_simple_total_time += tmp
        print("simple : " + str(tmp) + " sec")

        # test de notre algo (simple + kmp)
        start_time = time.time()
        os.system('java -cp out/production/workspace/ Main2_screwed ' + regex + ' ' + TEXTE + ' -fk -p3 &> /dev/null')
        tmp = (time.time() - start_time)
        ours_kmp_total_time += tmp
        print("kmp : "+ str(tmp) + " sec")

        # test de notre algo (simple + kmp + multi-coeur)
        start_time = time.time()
        os.system('java -cp out/production/workspace/ Main2_screwed ' + regex + ' ' + TEXTE + ' -fkm -p3 &> /dev/null')
        tmp = (time.time() - start_time)
        ours_kmp_multi_total_time += tmp
        print("multi : " + str(tmp) + " sec")

        # test d'egrep
        start_time = time.time()
        os.system('egrep ' + regex + ' ' + TEXTE + ' &> /dev/null')
        tmp = (time.time() - start_time)
        egrep_total_time += tmp
        print("egrep : " + str(tmp) + " sec")

    ours_simple_total_time = ours_simple_total_time/NB_TESTS
    ours_kmp_total_time = ours_kmp_total_time/NB_TESTS
    ours_kmp_multi_total_time = ours_kmp_multi_total_time/NB_TESTS
    egrep_total_time = egrep_total_time/NB_TESTS



    resultats[regex] = dict()
    resultats[regex]["ours_simple"] = ours_simple_total_time
    resultats[regex]["ours_kmp"] = ours_kmp_total_time
    resultats[regex]["ours_kmp_multi"] = ours_kmp_multi_total_time
    resultats[regex]["egrep"] = egrep_total_time

ours_simple = []
for regex, method in resultats.items():
    ours_simple.append(method["ours_simple"])

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
largeur_totale = 1.80
largeur = largeur_totale/(len(ours_kmp) * 1.3)

# Création de la figure et d'un set de sous-graphiques
fig, ax = plt.subplots()
r1 = ax.bar(position - largeur*2 + largeur/2, ours_simple, largeur)
r2 = ax.bar(position - largeur + largeur/2, ours_kmp, largeur)
r3 = ax.bar(position + largeur - largeur/2, ours_kmp_multi, largeur)
r4 = ax.bar(position + largeur*2 - largeur/2, egrep, largeur)


simple_patch = mpatches.Patch(color='blue', label='Simple')
kmp_patch = mpatches.Patch(color='orange', label='Simple + KMP')
multi_patch = mpatches.Patch(color='green', label='Simple + KMP + Multi-coeur')
egrep_patch = mpatches.Patch(color='red', label='egrep')
plt.legend(handles=[simple_patch,kmp_patch,multi_patch,egrep_patch])

# Modification des marques sur l'axe des x et de leurs étiquettes
ax.set_xticks(position)
ax.set_xticklabels(REGEX_A_TESTER)


plt.savefig('test_classique_50000_2.svg')
#plt.show()

print(resultats)


