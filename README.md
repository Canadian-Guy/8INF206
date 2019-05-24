# 8INF206
Cours d'été 2019, application d'archivage

Documentation pour l’application d’archivage faite dans le cadre du cours 8INF206
Nom : Jason Morin

ÉcranPrincipal.java : Comme le nom l’indique, il s’agit de l’écran principal. C’est à partir de cet écran que l’utilisateur peut accéder aux autres parties de l’application.

TagsActivity.java : Écran de gestion des tags. Cette activité permet à l’utilisateur de gerer ses tags. Une boite de texte permet d’entrer un nom, et le bouton « + » permet d’ajouter le tag, ce qui aura pour effet de l’ajouter à la base de données. Il y a un listener sur la base de données au niveau des tags de l’utilisateur, ce qui permet de rafraichir la liste de tags à afficher à chaque fois qu’une valeur est modifiée. 
Cette activité contient aussi un adaptateur pour afficher le nom des tags ainsi qu’un bouton pour leur suppression. 

Tag.java : Objet tag, qui implémente l’interface Parcelable. Il sert à contenir le nom du tag ainsi qu’un booléen qui indique s’il est sélectionné ou non. Ceci est utile pour l’affichage et la sélection de tags dans une listView composée de checkBox. Le booléen évite de perdre l’information lorsque le tag sort de l’écran. 
La classe implémente l’interface Parcelable pour permettre de sauvegarder une liste de tags dans un bundle affin de faciliter la gestion du cycle de vie de l’activité d’archivage.

TagListAdapter.java : Simple adaptateur qui permet d’afficher le nom des tags qui lui sont envoyés ainsi qu’un bouton pour la suppression. Cet adaptateur est utilisé dans l’activité de gestion des tags pour afficher les tags existants.

ArchiverActivity.java : Écran pour l’archivage de photo. Il permet de sélectionner des tags grâce à un adaptateur et d’une listView, d’ajouter une description, de choisir ou encore de prendre une photo et d’avoir un aperçu de la photo. 
Les tags proposés à l’utilisateur sont récupérés dans la base de données au lancement de l’activité. 
Lors de l’archivage, on sauvegarde les tags selectionnés, la description de la photo ainsi qu’un lien vers la photo dans la RealtimeDatabase, et on sauvegarde la photo dans le Storage. Ensuit, on réinitialise chaque champ pour être prêt à archiver une autre photo.

TagSelectionAdapter.java : Adaptateur pour afficher les tags sous forme de checkbox. Le champ « isSelected » des tags détermine si leur checkbox est cochée ou non, et leur nom est affichée comme texte de la checkbox. Cet adapateur est utilisé dans l’activité d’archivage et dans l’activité de récupération.

RecupererActivity.java : Écran pour la récupération de photos. L’écran est composé d’une liste de photos et d’une liste de tags. La liste de photo affiche les photos grâce à l’adaptateur « PhotoListAdapter », et la liste de tags utilise le même adaptateur que l’activité d’archivage. Il permet de sélectionner des tags comme l’écran d’archivage, mais quand un tag est sélectionné, il est aussi ajouté dans une liste « SelectedTags » qui sera utilisée pour filtrer les photos. 
À chaque fois qu’un tag est sélectionné, la liste de photos est purgée et on récupère les photos de nouveau en fonction du nouveau filtre. Les éléments de la liste de photos peuvent être cliqués pour afficher la photo correspondante en plus grand. 

Photo.java : Simple objet qui contient les informations d’une photo. Il contient un le timestamp correspondant à la photo (clé de la photo dans la base de données), la description que l’utilisateur a peut-être entrée ainsi que le lien https pour voir la photo. 

PhotoListAdapter.java : Adaptateur qui permet d’afficher une photo sous la forme suivante : une petite image, la description de la photo et un bouton pour la suppression. 
Pour l’affichage de la photo, l’adaptateur récupère la photo dans le Storage grâce au lien contenue dans l’objet photo qui lui est passé. L’adaptateur gère le click du bouton de suppression pour faciliter cette dernière. En effet, l’adaptateur a déjà toutes les information nécessaires pour supprimer l’entrée dans la base de donnée (le timestamp) ainsi que dans le Storage (le lien).

MainActivity.java : C’est à partir de cette activité que l’interface de connexion fournit par FirebaseAuthentication est ouvert. Si l’utilisateur le ferme par mégarde, il peut le rouvrir en appuyant sur le bouton connexion, qui est le seul élément de l’écran MainActivity.
