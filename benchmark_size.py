import datetime
import os
import pathlib
import time
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import numpy as np
import re

import collections

DOSSIER_TESTS = "tests/"
#REGEX_A_TESTER = ["account", "the", "whithdz", "whitwhi", "amamo", "'a|bc*'", "'wh(at.*|o)'"]
REGEX_A_TESTER = "'wh(at.*|o)'"
NB_TESTS = 1

ours_simple_total_time = []
ours_kmp_total_time = []
ours_kmp_multi_total_time = []
egrep_total_time = []

files = dict()
for p in pathlib.Path(DOSSIER_TESTS).iterdir():
    if p.is_file():
        #file = open(p)
        path = str(p.absolute())
        files[os.path.getsize(path)] = path

for size, path in collections.OrderedDict(sorted(files.items())).items():

    # test de notre algo (simple)
    start_time = time.time()
    os.system('java -cp out/production/workspace/ Main2_screwed ' + REGEX_A_TESTER + ' ' + path + ' -f -p3 &> /dev/null')
    tmp = (time.time() - start_time)
    ours_simple_total_time.append(tmp)
    print("simple : " + str(tmp) + " sec")

    # test de notre algo (simple + kmp)
    start_time = time.time()
    os.system('java -cp out/production/workspace/ Main2_screwed ' + REGEX_A_TESTER + ' ' + path + ' -fk -p3 &> /dev/null')
    tmp = (time.time() - start_time)
    ours_kmp_total_time.append(tmp)
    print("kmp : "+ str(tmp) + " sec")

    # test de notre algo (simple + kmp + multi-coeur)
    start_time = time.time()
    os.system('java -cp out/production/workspace/ Main2_screwed ' + REGEX_A_TESTER + ' ' + path + ' -fkm -p3 &> /dev/null')
    tmp = (time.time() - start_time)
    ours_kmp_multi_total_time.append(tmp)
    print("multi : " + str(tmp) + " sec")

    # test d'egrep
    start_time = time.time()
    os.system('egrep ' + REGEX_A_TESTER + ' ' + path + ' &> /dev/null')
    tmp = (time.time() - start_time)
    egrep_total_time.append(tmp)
    print("egrep : " + str(tmp) + " sec")




# generation de l'axe x
x = []
for i in range(50000, 5050000, 50000):
    x.append(i)



# plot the 3 sets
plt.plot(x,ours_simple_total_time,label='Simple')
plt.plot(x,ours_kmp_total_time, label='Simple + KMP')
plt.plot(x,ours_kmp_multi_total_time, label='Simple + KMP + Multi-coeur')
plt.plot(x,egrep_total_time, label='egrep')

# call with no parameters
plt.legend()

plt.savefig('test_size4.svg')
plt.show()
