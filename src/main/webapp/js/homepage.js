(function () {

        var userAlbumsTable, othersAlbumsTable;
        var presentation;
        var currentAlbumImages = [];
        var images = [];
        var currentOffset = 0;
        var itemsPerPage = 5;
        var serverError = document.getElementById("serverError");

        //al caricamento della pagina
        window.addEventListener("load", () => {

            if (sessionStorage.getItem("id") == null) {
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

            //Prendi gli album dal server con AJAX
            loadAlbums();
            loadUserImages();

            var toggle = document.getElementById('toggle');
            toggle.style.display = "block";

            toggle.addEventListener('click', () => {
                var x = document.getElementById('createAlbumFormContainer');
                if (x.style.display === "none" || x.style.display === "") {
                    toggle.textContent = "Hide form";
                    x.style.display = "block";
                    document.getElementById("userAlbumsContainer").style.display = "none";
                } else {
                    toggle.textContent = "Create new album";
                    x.style.display = "none";
                    document.getElementById("userAlbumsContainer").style.display = "block";
                }
            });

            document.getElementById('createAlbumButton').addEventListener('click', function (event) {
                event.preventDefault();
                //mostra la lista aggiornata
                document.getElementById('toggle').textContent = "Create new album";
                document.getElementById('createAlbumFormContainer').style.display = "none";
                document.getElementById("userAlbumsContainer").style.display = "block";

                let form = document.getElementById("createAlbumForm");
                const data = new FormData(form);
                makePost('createAlbum', data, addAlbum);
            });

            document.getElementById('albumImagesDiv').style.display = "none";

            //Mostra tasto previous
            document.getElementById('prevButton').addEventListener('click', () => {
                currentOffset -= itemsPerPage;
                displayImages(images);
            });


            //Mostra tasto next
            document.getElementById('nextButton').addEventListener('click', () => {
                currentOffset += itemsPerPage;
                displayImages(images);
            });


        }, false);

        function loadAlbums() {
            userAlbumsTable.reset();
            othersAlbumsTable.reset();
            makeGet("getAlbums", handleAlbums);
        }

        function loadUserImages() {
            makeGet("getUserImages", insertList);
        }

        function AlbumTable(_listcontainer, _listcontainerbody) {
            this.listcontainer = _listcontainer;
            this.listcontainerbody = _listcontainerbody;

            this.reset = function () {
                this.listcontainerbody.innerHTML = "";
                this.listcontainer.style.display = "none";
            }

            this.show = function (albums) {
                this.update(albums);
            };

            this.update = function (albums) {
                let row, titleCell, dateCell, linkcell, anchor;

                if (albums.length === 0) {
                    presentation.textContent = "No album uploaded"

                } else {
                    this.listcontainerbody.innerHTML = "";

                    let self = this;
                    albums.sort((a, b) => new Date(b.date) - new Date(a.date));

                    albums.forEach(function (album) {
                        row = document.createElement("tr");

                        // titolo
                        titleCell = document.createElement("td");
                        titleCell.textContent = album.title;
                        row.appendChild(titleCell);

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
                        button.setAttribute('albumId', album.id);
                        button.addEventListener("click", (e) => {
                            e.preventDefault();
                            makeGet("album?id=" + album.id, (req) => {
                                if (req.readyState === XMLHttpRequest.DONE) {
                                    if (req.status === 200) {
                                        serverError.style.display = "none";

                                        images = JSON.parse(req.responseText);  //Prendo tutte le immagini
                                        document.getElementById("albumTile").textContent = album.title;
                                        document.getElementById("albumCreator").textContent = album.authorUsername;
                                        document.getElementById("albumDate").textContent = album.date;
                                        currentOffset = 0;
                                        displayImages(images);
                                    } else {
                                        serverError.textContent = "Server error [" + req.status + "]: " + req.responseText;
                                        serverError.style.display = "block";
                                    }
                                }
                            });
                        }, false);

                        linkcell.appendChild(button);
                        row.appendChild(linkcell);

                        self.listcontainerbody.appendChild(row);
                    });

                    this.listcontainer.style.display = "block";
                }
            }
        }

        //Funzioni di callback

        function handleAlbums(req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                if (req.status === 200) {
                    serverError.style.display = "none";

                    let allAlbums = JSON.parse(req.responseText);
                    console.log(allAlbums);

                    let username = sessionStorage.getItem('username');

                    //filtra album personali e degli altri utenti
                    let userAlbums = allAlbums.filter(album => album.username === username);
                    let otherAlbums = allAlbums.filter(album => album.username !== username);

                    userAlbumsTable.show(userAlbums);
                    othersAlbumsTable.show(otherAlbums);
                } else {
                    serverError.textContent = "Server error [" + req.status + "]: " + req.responseText;
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
                    newButton.setAttribute('albumId', newAlbum.id);
                    newButton.addEventListener("click", (e) => {
                        e.preventDefault();
                        makeGet("album?id=" + newAlbum.id, (req) => {
                            if (req.readyState === XMLHttpRequest.DONE) {
                                if (req.status === 200) {
                                    serverError.style.display = "none";

                                    images = JSON.parse(req.responseText);  //Prendo tutte le immagini
                                    document.getElementById("albumTile").textContent = newAlbum.title;
                                    document.getElementById("albumCreator").textContent = newAlbum.authorUsername;
                                    document.getElementById("albumDate").textContent = newAlbum.date;
                                    currentOffset = 0;
                                    displayImages(images);
                                } else {
                                    serverError.textContent = "Server error [" + req.status + "]: " + req.responseText;
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
                    document.getElementById("userAlbumsContainer").style.display = "block";

                    //nascondi form creazione
                    document.getElementById('toggle').textContent = "Create new album";
                    document.getElementById('createAlbumFormContainer').style.display = "none";
                } else {
                    serverError.textContent = "Server error [" + req.status + "]: " + req.responseText;
                    serverError.style.display = "block";
                }
            }
        }

        function insertList(req) {
            if (req.readyState === XMLHttpRequest.DONE) {
                if (req.status === 200) {
                    serverError.style.display = "none";

                    let images = JSON.parse(req.responseText);
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
                    serverError.textContent = "Server error [" + req.status + "]: " + req.responseText;
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

            document.getElementById("closeAlbum").onclick = function () {
                document.getElementById('toggle').style.display = "block";
                albumImagesDiv.style.display = "none";
            }

            // Clear existing images but keep prev and next buttons
            while (imageRow.childElementCount > 2) {
                imageRow.removeChild(imageRow.children[1]);
            }

            currentAlbumImages = images.slice(currentOffset, currentOffset + itemsPerPage);

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
                img.setAttribute("author",image.idAuthor);
                card.appendChild(img);

                let title = document.createElement("span");
                title.textContent = image.title;
                card.appendChild(title);

                img.addEventListener('mouseenter', () => {
                    showModal(image);
                });

                imageRow.insertBefore(cell, imageRow.children[imageRow.childElementCount - 1]);
            });

            let emptyCellsToAdd = itemsPerPage - currentAlbumImages.length;
            for (let i = 0; i < emptyCellsToAdd; i++) {
                let emptyCell = document.createElement("td");
                emptyCell.style.width = "20%";
                imageRow.insertBefore(emptyCell, imageRow.children[imageRow.childElementCount - 1]);
            }

            albumImagesContainer.style.display = "block";

            document.getElementById('prevButton').style.display = currentOffset > 0 ? "table-cell" : "none";
            document.getElementById('nextButton').style.display = currentOffset + itemsPerPage < images.length ? "table-cell" : "none";

            //Funzioni finestra modale

            var closeButton = document.getElementById("closeImage");
            var commentsList = document.getElementById("commentsList");

            function addComment(req) {
                if (req.readyState === XMLHttpRequest.DONE) {
                    if (req.status === 200) {
                        serverError.style.display = "none";

                        let comment = JSON.parse(req.responseText);
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
                        serverError.textContent = "Server error [" + req.status + "]: " + req.responseText;
                        serverError.style.display = "block";
                    }
                }
            }

            function showModal(image) {
                document.getElementById("modalImage").src = image.path;
                document.getElementById("modalTitle").textContent = image.title;
                document.getElementById("modalAuthor").textContent = image.idAuthor;
                document.getElementById("modalDate").textContent = image.date;

                var deleteContainer = document.getElementById("deleteImageContainer");
                deleteContainer.style.display = parseInt(sessionStorage.getItem("id")) === image.idAuthor ? "block" : "none";

                document.getElementById("deleteImageButton").addEventListener('click', function (event) {
                    event.preventDefault();

                    let form = document.getElementById("deleteImageForm");
                    const data = new FormData(form);
                    data.append("imageId",image.id);

                    makePost("deleteImage",data,(req) => {
                        if (req.readyState === XMLHttpRequest.DONE) {
                            if (req.status === 200) {
                                serverError.style.display = "none";

                                hideModal();
                                albumImagesDiv.style.display = "none";
                                images.forEach(i =>{
                                    if (i.id === image.id)
                                        images.remove(i);
                                })
                                displayImages(images);
                            } else {
                                serverError.textContent = "Server error [" + req.status + "]: " + req.responseText;
                                serverError.style.display = "block";
                                hideModal();
                            }
                        }
                    });
                });

                var comments = image.commentList;

                commentsList.innerHTML = "";
                comments.forEach(comment => {
                    let listItem = document.createElement("li");
                    listItem.classList.add("list-group-item");
                    listItem.textContent = comment.username + ": " + comment.text;
                    commentsList.appendChild(listItem);
                });

                document.getElementById('addCommentButton').addEventListener('click', function (event) {
                    event.preventDefault();

                    let form = document.getElementById("addCommentForm");
                    const data = new FormData(form);

                    // Controllo input testo
                    if (document.getElementById('commentText').value.trim() === "") {
                        alert("Comment cannot be empty!");
                        return;
                    }
                    data.append('imageId', image.id);

                    makePost('addComment', data, addComment);
                });

                document.getElementById("modalContainer").style.display = "block";
            }

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

    }

)
();
