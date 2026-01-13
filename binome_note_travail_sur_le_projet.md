
Ce projet est réalisé par :
- Njeh Adam
- Zaabar Azer Hassine

# Notes du binôme pour expliquer le travail réalisé et pour répondre aux questions sur le projet

Cette page au format
[MarkDown](https://docs.gitlab.com/ee/user/rich_text_editor.html#input-rules
"MarkDown") sert (1) à indiquer l'état d'avancement des travaux dans
le projet MiniJAVA, (2) à fournir vos réponses aux questions qui sont
posées dans l'énoncé, et (3) à toute autre information permettant de
mieux comprendre ce qui a été fait, ce qui est en cours de réalisation
sans être finalisé, et ce qui pose problème.

## Phase 1 : Analyse lexicale et syntaxique

### État d'avancement des travaux sur la phase

- [x] phase 1 : terminée
- [] phase 2 : en cours
- [] phase 3 : non commencé
- [] À FAIRE 3 : terminé/presque terminé/en cours/commencé/non commencé
### Questions

**1. est-il possible de définir la valeur d'un entier sous la forme octale ?**

 Il n'est pas possible de définir un entier sous forme octale. La règle pour les nombres entiers est la suivante :
 ``` java
 Integer = 0 | [1-9] [0-9]*
```
Cela signifie qu'un entier est soit le chiffre 0 seul, soit un nombre commençant par un chiffre de 1 à 9 suivi par d'autres chiffres. Les littéraux commençant par 0 suivi d'autres chiffres (comme 07 ou 012 pour la notation octale) ne sont pas autorisés et provoqueront une erreur lexicale. Seuls les entiers en notation décimale sont acceptés.


 **2. dans une méthode, peut-on écrire « int a; a = 0; int b; b = n; » ? ou doit-on plutôt écrire « int a; int b; a = 0; b = n; » ?**

 Oui, les deux formes sont correctes. La grammaire du langage autorise l'entrelacement des déclarations de variables et des instructions dans un même bloc de méthode.

 **3. est-il possible de mettre un attribut de classe (static) dans la classe qui contient la méthode main ?**

 Non, ce n'est pas possible. La définition de la classe qui contient la méthode main est très stricte. Cette classe spéciale, définie par la règle klassMain, ne peut contenir que la méthode main et rien d'autre : ni attributs (qu'ils soient static ou non), ni autres méthodes.

De plus, le mot-clé static n'est autorisé que pour la déclaration de la méthode main. Il n'est pas possible de déclarer des attributs static dans aucune classe.

**4. est-il possible d'avoir une variable locale dans la méthode main ?**

Oui, c'est tout à fait possible. La grammaire permet à la méthode main d'avoir un corps qui est un bloc d'instructions. À l'intérieur de ce bloc, on peut déclarer des variables locales, tout comme dans n'importe quelle autre méthode.

### Commentaires du binôme sur la phase :

VOTRE TEXTE ICI ou SUPPRIMER CETTE LIGNE SI PAS DE COMMENTAIRE

## Phase N : Titre de la phase

### État d'avancement des travaux sur la phase

...

### Commentaires du binôme sur la phase :
