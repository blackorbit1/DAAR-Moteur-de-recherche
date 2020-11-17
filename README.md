# DAAR-Moteur-de-recherche
Moteur permettant une recherche sur un texte via RegEx

## Utilisation
Par defaut, requiert 2 arguments : regex et string, cherche
si la string contient la regex, renvoie true/false ou 1/0 pour bash

Options :
- `-f` pour lire un fichier
- `-v` pour afficher toutes les etapes
- `-pX` pour la precision, X dans {1,2,3}
- `-m` pour l'utilisation de tous les coeurs du processeur
- `-k` pour l'utilisation de l'algorithme de KMP
