(function () {      //IIFE: evita l'utilizzo di variabili nel global scope

        //elementi della pagina
        var userAlbumsTable, othersAlbumsTable;
        var currentAlbumImages = [];
        var images = [];        //Tutte le immagini dell'album corrente
        var order = [];        //Ordine di tutte le immagini dell'album corrente
        var originalOrder = [];
        var currentOffset = 0;
        var serverError = document.getElementById("serverError");

        //al caricamento della pagina
        window.addEventListener("load", () => {

            //controllo anche lato client se la sessione è attiva
            if (sessionStorage.getItem("id") == null || sessionStorage.getItem("username") == null) {
                window.location.href = "index.html";
            }

            //mostra messaggio personalizzato
            document.getElementById("id_username").textContent = sessionStorage.getItem("username");

            userAlbumsTable = new AlbumTable(
                document.getElementById("userAlbumsContainer"),
                document.getElementById("userAlbums")
            );

            othersAlbumsTable = new AlbumTable(
                document.getElementById("othersAlbumsContainer"),
                document.getElementById("othersAlbums")
            );

            serverError.style.display = "none";
            document.getElementById("saveOrderButton").style.visibility = "none";

            //fetch dei dati necessari alla pagina in modo asincrono
            loadAlbums();
            loadUserImages();

            document.querySelector(".toggle-form").style.display = "none";

            var toggle = document.getElementById('toggle');
            toggle.style.display = "block";

            toggle.addEventListener('click', () => {
                var x = document.getElementById('createAlbumFormContainer');
                if (x.style.display === "none" || x.style.display === "") {
                    toggle.textContent = "Hide form";
                    x.style.display = "block";
                    document.querySelector(".toggle-form").style.display = "block";
                    document.getElementById("userAlbumsContainer").style.display = "none";
                } else {
                    toggle.textContent = "Create new album";
                    x.style.display = "none";
                    document.querySelector(".toggle-form").style.display = "none";
                    document.getElementById("userAlbumsContainer").style.display = "table";
                }
            });

            document.getElementById('createAlbumButton').addEventListener('click', function (event) {
                event.preventDefault();
                //mostra la lista aggiornata
                document.getElementById('toggle').textContent = "Create new album";
                document.getElementById('createAlbumFormContainer').style.display = "none";
                document.getElementById("userAlbumsContainer").style.display = "table";

                let form = document.getElementById("createAlbumForm");
                const data = new FormData(form);
                makePost('createAlbum', data, addAlbum);
            });

            //se schiaccio invio mentre scrivo simula il click del tasto submit
            document.getElementById('title').addEventListener('keypress', function (event) {
                if (event.key === "Enter") {
                    event.preventDefault();
                    document.getElementById('createAlbumButton').click();
                }
            });

            document.getElementById('albumImagesDiv').style.display = "none";

            //Mostra tasto previous
            document.getElementById('prevButton').addEventListener('click', () => {
                currentOffset -= 5;
                displayImages(images);
            });

            //Mostra tasto next
            document.getElementById('nextButton').addEventListener('click', () => {
                currentOffset += 5;
                displayImages(images);
            });

        }, false);

        //funzioni di data fetch asincrone

        function loadAlbums() {
            userAlbumsTable.reset();
            othersAlbumsTable.reset();
            makeGet("getAlbums", handleAlbums);
        }

        function loadUserImages() {
            makeGet("getUserImages", insertList);
        }

        //oggetto albumTable

        function AlbumTable(_listcontainer, _listcontainerbody) {
            this.listcontainer = _listcontainer;
            this.listcontainerbody = _listcontainerbody;

            //svuota e nascondi il contenuto precedente
            this.reset = function () {
                this.listcontainerbody.innerHTML = "";
                this.listcontainer.style.display = "none";
            }

            this.update = function (albums) {
                let row, titleCell, dateCell, linkcell, anchor;

                if (albums.length === 0) {
                    document.getElementById("noAlbumsMessage").textContent = "No album uploaded yet";
                    document.getElementById("noAlbumsMessage").style.display = "block";
                    this.listcontainerbody.style.display = "none";
                } else {
                    this.listcontainerbody.innerHTML = "";
                    this.listcontainer.style.display = "table";

                    let self = this;

                    //riordina in ordine decrescente di data
                    albums.sort((a, b) => new Date(b.date) - new Date(a.date));

                    albums.forEach(function (album) {

                        row = document.createElement("tr");

                        // titolo
                        titleCell = document.createElement("td");
                        titleCell.textContent = album.title;
                        row.appendChild(titleCell);

                        // creatore (solo per others)
                        if(album.username !== sessionStorage.getItem("username")){
                            creatorCell = document.createElement("td");
                            creatorCell.textContent = album.username;
                            row.appendChild(creatorCell);
                        }

                        // data
                        dateCell = document.createElement("td");
                        dateCell.textContent = album.date;
                        row.appendChild(dateCell);

                        // link
                        linkcell = document.createElement("td");
                        button = document.createElement("button");
                        button.textContent = "View album";
                        button.classList.add("btn");
                        button.classList.add("btn-info");
                        button.addEventListener("click", (e) => {
                            if(document.getElementById("editOrderContainer").style.display === "block"){
                                serverError.textContent = "please close or submit the current album order first";
                                serverError.style.display = "block";
                            }
                            else{
                            e.preventDefault();
                            makeGet("album?id=" + album.id, (request) => {
                                if (request.readyState === XMLHttpRequest.DONE) {       //4
                                    if (request.status === 200) {                       //ok
                                        serverError.style.display = "none";
                                        serverError.style.color = "red";

                                        images = JSON.parse(request.responseText);  //Prendo tutte le immagini (var globale)

                                        document.getElementById("albumTitle").textContent = album.title;
                                        document.getElementById("albumCreator").textContent = album.username;
                                        document.getElementById("albumDate").textContent = album.date;

                                        document.getElementById("editOrderButton").style.display = "none";

                                        if (images.length === 0) {
                                            serverError.textContent = "this album is empty";
                                            serverError.style.display = "block";
                                        } else {
                                            currentOffset = 0;

                                            order = [];

                                            //Stabilisco ordine immagini
                                            if (album.imagesOrder === null || album.imagesOrder === undefined) {
                                                // Riordina in ordine decrescente di data
                                                for (i = 0; i < images.length; i++)
                                                    order[i] = images[i].id;
                                                console.log('Order (default):', order);
                                            } else {
                                                // Carica ordine dal campo imagesOrder
                                                order = album.imagesOrder.split(',').map(id => parseInt(id, 10));
                                                console.log('Order (from database):', order);
                                            }

                                            originalOrder = [...order];

                                            displayImages(images);

                                            document.getElementById("closeAlbum").onclick = function () {
                                                document.getElementById('toggle').style.display = "block";
                                                albumImagesDiv.style.display = "none";
                                                document.getElementById('editOrderContainer').style.display = "none";
                                                document.getElementById('serverError').style.display = "none";
                                                //riabilita bottoni
                                                document.querySelectorAll(".btn-info").forEach(button => {
                                                    button.disabled = false;
                                                });
                                            }

                                            //accedo alla modifca se ho più di un immagine
                                            if (images.length > 1)
                                                document.getElementById("editOrderButton").style.display = "block";
                                            else
                                                document.getElementById("editOrderButton").style.display = "none";

                                            document.getElementById("saveOrderButton").style.display = "none";

                                            document.getElementById('editOrderButton').addEventListener('click', () => {
                                                //disabilità bottoni di album view per non creare interferenze
                                                document.querySelectorAll(".btn-info").forEach(button => {
                                                    button.disabled = true;
                                                });

                                                document.getElementById('albumImagesContainer').style.display = "none";
                                                document.getElementById('editOrderContainer').style.display = "block";
                                                document.getElementById('backToViewButton').style.display = "block";
                                                document.getElementById("editOrderButton").style.display = "none";
                                                document.getElementById("saveOrderButton").style.display = "block";
                                                document.getElementById("albumDivTitle").textContent = "Album Reorder";

                                                loadSortableList();
                                            });

                                            document.getElementById('backToViewButton').addEventListener('click', () => {
                                                //riabilita bottoni
                                                document.querySelectorAll(".btn-info").forEach(button => {
                                                    button.disabled = false;
                                                });

                                                document.getElementById('editOrderContainer').style.display = "none";
                                                displayImages(images);
                                            });

                                            document.getElementById("saveOrderButton").addEventListener("click", () => {
                                                console.log("Final Order: ", order);

                                                //riabilita bottoni
                                                document.querySelectorAll(".btn-info").forEach(button => {
                                                    button.disabled = true;
                                                });

                                                let form = document.getElementById("saveOrderForm");
                                                const data = new FormData(form);
                                                data.append("order", order.toString());
                                                data.append("albumId", album.id);

                                                makePost("saveOrder", data, (request) => {
                                                    if (request.readyState === XMLHttpRequest.DONE) {
                                                        if (request.status === 200) {
                                                            serverError.textContent = "Order updated successfully! The changes will be visible on your next login.";
                                                            serverError.style.color = "green";
                                                            serverError.style.display = "block";
                                                        } else {
                                                            serverError.textContent = "Server error [" + request.status + "]: " + request.responseText;
                                                            serverError.style.display = "block";
                                                        }
                                                    }
                                                });

                                                //Totna alla visualizzazione delle liste
                                                document.getElementById('editOrderContainer').style.display = "none";
                                                document.getElementById('toggle').style.display = "block";
                                                document.getElementById('albumImagesDiv').style.display = "none";
                                                document.getElementById('editOrderContainer').style.display = "none";
                                                document.getElementById('serverError').style.display = "none";

                                                //riabilita bottoni
                                                document.querySelectorAll(".btn-info").forEach(button => {
                                                    button.disabled = false;
                                                });
                                            });
                                        }

                                    } else {
                                        serverError.textContent = "Server error [" + request.status + "]: " + request.responseText;
                                        serverError.style.display = "block";
                                    }
                                }
                            });

                            }
                        }, false);

                        linkcell.appendChild(button);
                        row.appendChild(linkcell);

                        self.listcontainerbody.appendChild(row);
                    });
                }
            }
        }

        //Funzioni di callback

        function handleAlbums(request) {
            if (request.readyState === XMLHttpRequest.DONE) {
                if (request.status === 200) {
                    serverError.style.display = "none";

                    let allAlbums = JSON.parse(request.responseText);

                    let username = sessionStorage.getItem('username');

                    //filtra album personali e degli altri utenti
                    let userAlbums = allAlbums.filter(album => album.username === username);
                    let otherAlbums = allAlbums.filter(album => album.username !== username);

                    userAlbumsTable.update(userAlbums);
                    othersAlbumsTable.update(otherAlbums);
                } else {
                    serverError.textContent = "Server error [" + request.status + "]: " + request.responseText;
                    serverError.style.display = "block";
                }
            }
        }

        function addAlbum(request) {
            if (request.readyState === XMLHttpRequest.DONE) {
                if (request.status === 200) {
                    serverError.style.display = "none";

                    let newAlbum = JSON.parse(request.responseText);

                    document.getElementById("userAlbumsContainer").style.display = "none";

                    newRow = document.createElement("tr");

                    // titolo
                    newTitleCell = document.createElement("td");
                    newTitleCell.textContent = newAlbum.title;
                    newRow.appendChild(newTitleCell);

                    // data
                    newDateCell = document.createElement("td");
                    newDateCell.textContent = newAlbum.date;
                    newRow.appendChild(newDateCell);

                    // link
                    newLinkcell = document.createElement("td");
                    newButton = document.createElement("button");
                    newButton.textContent = "View album";
                    newButton.classList.add("btn");
                    newButton.classList.add("btn-info");
                    newButton.addEventListener("click", (e) => {
                        e.preventDefault();
                        makeGet("album?id=" + newAlbum.id, (request) => {
                            if (request.readyState === XMLHttpRequest.DONE) {
                                if (request.status === 200) {
                                    serverError.style.display = "none";

                                    images = JSON.parse(request.responseText);  //Prendo tutte le immagini
                                    document.getElementById("albumTitle").textContent = newAlbum.title;
                                    document.getElementById("albumCreator").textContent = newAlbum.authorUsername;
                                    document.getElementById("albumDate").textContent = newAlbum.date;
                                    currentOffset = 0;

                                    displayImages(images);
                                } else {
                                    serverError.textContent = "Server error [" + request.status + "]: " + request.responseText;
                                    serverError.style.display = "block";
                                }
                            }
                        });
                    });
                    //aggiungi in coda alla lista
                    newLinkcell.appendChild(newButton);
                    newRow.appendChild(newLinkcell);

                    //Aggiungi in cima perchè è il primo in ordine decrescente
                    document.getElementById("userAlbumsContainer").insertBefore(newRow, document.getElementById("userAlbumsContainer").firstChild);
                    document.getElementById("userAlbumsContainer").style.display = "table";

                    //nascondi form creazione
                    document.getElementById('toggle').textContent = "Create new album";
                    document.getElementById('createAlbumFormContainer').style.display = "none";

                } else {
                    serverError.textContent = "Server error [" + request.status + "]: " + request.responseText;
                    serverError.style.display = "block";
                }
            }
        }

        function insertList(request) {
            if (request.readyState === XMLHttpRequest.DONE) {
                if (request.status === 200) {
                    serverError.style.display = "none";

                    let images = JSON.parse(request.responseText);
                    let imagesContainer = document.getElementById("userImages");
                    imagesContainer.innerHTML = "";
                    images.forEach(image => {
                        let row = document.createElement("tr");

                        let selectCell = document.createElement("td");
                        let checkbox = document.createElement("input");
                        checkbox.type = "checkbox";
                        checkbox.name = "images";
                        checkbox.value = image.id;
                        selectCell.appendChild(checkbox);
                        row.appendChild(selectCell);

                        let titleCell = document.createElement("td");
                        titleCell.textContent = image.title;
                        row.appendChild(titleCell);

                        imagesContainer.appendChild(row);
                    });
                } else {
                    serverError.textContent = "Server error [" + request.status + "]: " + request.responseText;
                    serverError.style.display = "block";
                }
            }
        }

        function displayImages(images) {
            let albumImagesContainer = document.getElementById("albumImagesContainer");
            let albumImagesDiv = document.getElementById("albumImagesDiv");
            let imageRow = document.getElementById("imageRow");

            document.getElementById('toggle').style.display = "none";
            albumImagesDiv.style.display = "block";
            document.getElementById("albumDivTitle").textContent = "Album view";

            //rimuovo tutte le celle tranne i bottoni
            while (imageRow.childElementCount > 2) {
                imageRow.removeChild(imageRow.children[1]);
            }

            // Riordina immagini
            images.sort((a, b) => {
                const indexA = order.indexOf(a.id);
                const indexB = order.indexOf(b.id);
                if (indexA === -1 || indexB === -1) {
                    return 0; // se non sono presenti non faccio nulla
                }
                return indexA - indexB;
            });

            currentAlbumImages = images.slice(currentOffset, currentOffset + 5);

            currentAlbumImages.forEach(image => {

                let cell = document.createElement("td");
                cell.style.width = "20%";
                cell.style.verticalAlign = "top";

                let card = document.createElement("div");
                card.classList.add("d-flex");
                card.classList.add("flex-column");
                card.classList.add("align-items-center");
                cell.appendChild(card);

                let img = document.createElement("img");
                img.src = image.path;
                img.alt = image.title;
                img.className = "thumbnail";

                card.appendChild(img);

                let title = document.createElement("span");
                title.textContent = image.title;

                card.appendChild(title);

                img.addEventListener('mouseenter', () => {
                    showModal(image);
                });

                //Inserisco a partire dalla seconda posizione in ordine decrescente
                imageRow.insertBefore(cell, imageRow.children[imageRow.childElementCount - 1]);
            });

            //Genero celle vuote se necessario
            let emptyCellsToAdd = 5 - currentAlbumImages.length;
            for (let i = 0; i < emptyCellsToAdd; i++) {
                let emptyCell = document.createElement("td");
                emptyCell.style.width = "20%";
                imageRow.insertBefore(emptyCell, imageRow.children[imageRow.childElementCount - 1]);
            }

            albumImagesContainer.style.display = "block";

            document.getElementById('prevButton').style.display = currentOffset > 0 ? "table-cell" : "none";
            document.getElementById('nextButton').style.display = currentOffset + 5 < images.length ? "table-cell" : "none";

            //Funzioni finestra modale

            var closeButton = document.getElementById("closeImage");
            var commentsList = document.getElementById("commentsList");

            function addComment(request) {
                if (request.readyState === XMLHttpRequest.DONE) {
                    if (request.status === 200) {
                        serverError.style.display = "none";

                        let comment = JSON.parse(request.responseText);
                        var commentsList = document.getElementById("commentsList");
                        let listItem = document.createElement("li");

                        commentsList.style.display = "none";
                        listItem.classList.add("list-group-item");
                        listItem.textContent = comment.username + ": " + comment.text;
                        commentsList.appendChild(listItem);

                        commentsList.scrollTop = commentsList.scrollHeight;

                        document.getElementById("commentText").value = "";
                        document.getElementById("commentText").placeholder = "Type your comment";
                        commentsList.style.display = "block";
                    } else {
                        serverError.textContent = "Server error [" + request.status + "]: " + request.responseText;
                        serverError.style.display = "block";
                    }
                }
            }

            function showModal(image) {
                document.getElementById("modalImage").src = image.path;
                document.getElementById("modalTitle").textContent = image.title;
                document.getElementById("modalDescription").textContent = image.description;
                document.getElementById("modalAuthor").textContent = image.authorUsername;
                document.getElementById("modalDate").textContent = image.date;

                var deleteContainer = document.getElementById("deleteImageContainer");
                deleteContainer.style.display = parseInt(sessionStorage.getItem("id")) === image.idAuthor ? "block" : "none";

                document.getElementById("deleteImageButton").addEventListener('click', function (event) {
                    event.preventDefault();

                    let form = document.getElementById("deleteImageForm");
                    const data = new FormData(form);
                    data.append("imageId", image.id);

                    makePost("deleteImage", data, (request) => {
                        if (request.readyState === XMLHttpRequest.DONE) {
                            if (request.status === 200) {
                                serverError.style.display = "none";

                                hideModal();
                                albumImagesDiv.style.display = "none";
                                images.forEach(i => {
                                    if (i.id === image.id)
                                        images.remove(i);
                                })
                                displayImages(images);
                            } else {
                                serverError.textContent = "Server error [" + request.status + "]: " + request.responseText;
                                serverError.style.display = "block";
                                hideModal();
                            }
                        }
                    });
                });


                //Mostra commenti
                var comments = image.commentList;

                commentsList.innerHTML = "";
                comments.forEach(comment => {
                    let listItem = document.createElement("li");
                    listItem.classList.add("list-group-item");
                    listItem.textContent = comment.username + ": " + comment.text;
                    commentsList.appendChild(listItem);
                });

                //Form aggiungi commento
                document.getElementById('addCommentButton').addEventListener('click', function (event) {
                    event.preventDefault();

                    let form = document.getElementById("addCommentForm");
                    const data = new FormData(form);

                    // Controllo input testo lato client

                    if (document.getElementById('commentText').value.trim() === "") {
                        alert("Comment cannot be empty!");
                        return;
                    }
                    data.append('imageId', image.id);

                    makePost('addComment', data, addComment);
                });

                //se schiaccio invio mentre scrivo simula il click del tasto submit
                document.getElementById('commentText').addEventListener('keypress', function (event) {
                    if (event.key === "Enter") {
                        event.preventDefault();
                        document.getElementById('addCommentButton').click();
                    }
                });

                document.getElementById("modalContainer").style.display = "block";
            }

            //chiusure finestra modale

            function hideModal() {
                document.getElementById("modalContainer").style.display = "none";
            }

            closeButton.onclick = function () {
                hideModal();
            }

            window.onclick = function (event) {
                if (event.target === document.getElementById("modalContainer")) {
                    hideModal();
                }
            }
        }

        //Funzione per gestire il drag and drop tramite la libreria jquery

        function loadSortableList() {
            //prendi la lista e inizilizza
            let sortableList = $('#sortableImagesList');
            sortableList.empty();

            //popola la lista
            order.forEach(id => {
                let img = images.find(image => image.id === id);
                if (img) {
                    let row = $('<tr>').attr('data-id', img.id);

                    let titleCell = $('<td>').text(img.title);
                    let dateCell = $('<td>').text(img.date);

                    row.append(titleCell);
                    row.append(dateCell);

                    sortableList.append(row);
                }
            });

            //abilita ordinamento su ogni riga del body
            $('#sortableImagesList').sortable({
                items: 'tr',
                update: function (event, ui) {      //callback
                    order = sortableList.children().map(function () {       //aggiorna ordine
                        return $(this).data('id');
                    }).get();
                    console.log('New Order:', order);
                }
            });

            //disabilita selezione
            $('#sortableImagesTable').disableSelection();
        }
    }
)
(); //esegue immediatamente la funzione anonima
