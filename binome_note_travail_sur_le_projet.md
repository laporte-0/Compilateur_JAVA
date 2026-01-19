
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

## Phase 2 : 

### Questions

**1. lorsque l'on écrit un visiteur héritant du visiteur par défaut AstVisitorDefault, à quoi sert l'appel à la méthode defaultVisit dans les méthodes redéfinies ?**

`AstVisitorDefault` fournit un comportement de visite “standard” centralisé dans `defaultVisit` : typiquement, il assure la descente récursive dans les sous-nœuds (et la propagation des informations/attributs nécessaires au fil du parcours) de manière uniforme pour tous les nœuds. Quand on redéfinit une méthode `visit()` dans un visiteur concret, appeler `defaultVisit(n)` permet donc de conserver automatiquement ce parcours par défaut, tout en ajoutant seulement le traitement spécifique (contrôles sémantiques, collecte d’infos, ...). Ca évite de réécrire la logique de traversée à la main et surtout d’oublier de visiter certains enfants, ce qui rend les passes plus fiables et plus faciles à maintenir.

**2. lorsque l'on ajoute un nouveau type de nœud dans l'AST, faut-il modifier le visiteur par défaut AstVisitorDefault ? si oui, pourquoi ?**

Oui, si on ajoute un nouveau type de nœud dans l’AST, il faut généralement mettre à jour le “contrat” de visite en ajoutant la méthode `visit(NouveauNoeud n)` (dans l’interface `AstVisitor`), puis fournir une implémentation correspondante dans `AstVisitorDefault` (souvent en déléguant à `defaultVisit(n)`). Sinon, les visiteurs existants ne compilent plus (car une méthode obligatoire manque), et/ou le nouveau nœud risque de ne pas être parcouru automatiquement : on perd alors la traversée récursive “standard” (descente dans les enfants, propagation d’infos), ce qui peut provoquer des oublis de vérification sémantique ou de génération de code sur des sous-arbres.

**3. que fait notre compilateur en cas d'erreur dans la gestion de l'héritage (détection d'un cycle) ?**

En cas d’erreur sur l’héritage (par exemple un cycle), notre passe de vérification de l’héritage (ex. `checkInheritance`) détecte la boucle, enregistre/affiche un message d’erreur, puis signale l’échec de l’analyse sémantique. À la fin de la phase sémantique, le compilateur considère qu’il y a des “Semantic Error(s)” et interrompt la compilation en levant une exception (type `CompilerException`) : la compilation s’arrête donc avant les phases suivantes, et aucun code MIPS n’est généré.

**4. notre compilateur considère-t-il comme une erreur de redéfinition l'écrasement d'un paramètre d'appel par une variable locale ? Qu'en est-il du compilateur javac ?**

Dans notre compilateur, ce n’est pas considéré comme une redéfinition : les paramètres formels sont insérés dans une portée dédiée (portée des arguments), puis le corps de la méthode et les blocs (`StmtBlock`) créent des portées filles, comme l’insertion d’une variable ne vérifie les doublons que dans la portée courante, une variable locale peut donc porter le même nom qu’un paramètre (elle le masque/shadow). En revanche, avec le compilateur Java `javac`, redéclarer un identifiant déjà utilisé par un paramètre (même dans un bloc interne) est une erreur de compilation : un paramètre ne peut pas être masqué par une variable locale.

**5. notre compilateur accepte-t-il la séquence qui suit ? Qu'en est-il du compilateur javac ? `{ int a; a = 0; b = 0; int b; }`**

Notre compilateur l’accepte : même si le `int b;` apparaît après `b = 0;` dans le texte, la construction de la table des symboles insère d’abord toutes les déclarations locales du bloc, puis vérifie/visite les instructions ; ainsi, `b` est déjà connu au moment où l’affectation `b = 0;` est analysée. En revanche, `javac` refuse ce code : en Java, la portée d’une variable locale commence à sa déclaration, donc utiliser `b` avant `int b;` déclenche une erreur de compilation (variable non déclarée à cet endroit).

## Phase 3 : 

### Questions

**1. Le compilateur MiniJAVA gère-t-il la surcharge ? Si ce n'est pas le cas, que fait le compilateur ? (cf. aussi section 2.4 du Mémento MiniJAVA)**

Non, ce compilateur MiniJAVA ne gère pas la surcharge (overloading) : dans une même classe, on ne peut pas déclarer plusieurs méthodes portant le même nom avec des listes de paramètres différentes, car les méthodes sont indexées uniquement par leur nom dans la table des symboles. Ainsi, si on tente de redéfinir une méthode homonyme dans la même portée de classe, la construction de la table des symboles détecte une duplication d’identificateur (`Identifier already defined …`), signale une erreur sémantique et la compilation est interrompue (au lieu de sélectionner une "bonne" signature comme le ferait Java).

**2. Le compilateur MiniJAVA gère-t-il la redéfinition ? la liaison dynamique ? Si ce n'est pas le cas, que fait le compilateur ? (cf. aussi section 2.4 du Mémento MiniJAVA)**

La redéfinition (override) est prise en charge de façon partielle : une classe fille peut déclarer une méthode de même nom qu’une méthode héritée, ce qui la masque dans la table des symboles lors des recherches effectuées depuis la portée de la classe fille. En revanche, le compilateur ne fait pas de liaison dynamique (tardive) des appels de méthodes (pas de “dispatch” polymorphe) : un appel `e.m(...)` est résolu de manière statique à la compilation à partir du type **déclaré** du receveur `e` (la méthode recherchée est celle trouvée dans la classe correspondant à ce type, via l’héritage), et le code généré cible directement cette méthode. Conséquence, si l’objet réel à l’exécution est d’une sous-classe qui redéfinit `m`, cette redéfinition n’est pas sélectionnée automatiquement (elle ne sera utilisée que si le receveur est typé comme la sous-classe), ce qui limite/empêche le polymorphisme tel qu’en Java.

**3. Dans le code du visiteur TypeChecking, comment voit-on que le type est un attribut synthétisé ?**

On voit que `Type` est un attribut synthétisé parce qu’il est calculé **en remontant** à partir des sous-expressions : dans `TypeChecking`, les méthodes `visit(Expr...)` commencent typiquement par visiter les enfants (`accept(this)`), puis récupèrent leurs types via `getType(enfant)` et finissent par poser le type du nœud courant via `setType(n, ...)` (par exemple après un opérateur binaire/unnaire, un accès tableau, un appel de méthode, ...). Autrement dit, la valeur `Type(n)` n’est pas transmise par le parent (attribut hérité) mais produite par le nœud à partir des informations calculées sur ses fils et stockée dans `semanticTree.attrType()`, ce qui correspond exactement à la définition d’un attribut synthétisé.

**4. Pourquoi la méthode compareType appelée par la méthode checkType est-elle récursive ?**

`compareType(t1, t2)` est récursive parce qu’elle ne vérifie pas seulement l’égalité stricte des types : elle doit aussi décider si `t2` est compatible avec `t1` via le sous-typage (transtypage implicite **vers le haut** dans la hiérarchie des classes). Concrètement, si `t2` est un type de classe différent de `t1`, la méthode remonte la chaîne des ancêtres de `t2` (elle récupère la classe correspondant à `t2`, puis rappelle `compareType(t1, typeDuParentDe(t2))`, puis le parent du parent, ...) jusqu’à trouver `t1` ou atteindre la racine (`Object`/`null`). Cette récursion permet donc d’accepter des affectations/passage d’arguments du style « un `C` peut être utilisé là où on attend un `B` » lorsque `C extends B`.


## Phase N : Titre de la phase

### État d'avancement des travaux sur la phase

...

### Commentaires du binôme sur la phase :
