(function () {

    var userAlbumsTable, othersAlbumsTable;
    var presentation;
    var currentAlbumImages = [];
    var images = [];
    var currentOffset = 0;
    var itemsPerPage = 5;

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
            let l = albums.length;
            let row, titleCell, dateCell, linkcell, anchor;

            if (l === 0) {
                presentation.textContent = "No album uploaded"

            } else {
                this.listcontainerbody.innerHTML = "";

                let self = this;

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
                                    images = JSON.parse(req.responseText);  //Prendo tutte le immagini
                                    document.getElementById("albumTile").textContent = album.title;
                                    document.getElementById("albumCreator").textContent = album.authorUsername;
                                    document.getElementById("albumDate").textContent = album.date;
                                    currentOffset = 0;
                                    displayImages(images);
                                } else {
                                    console.error(req.responseText);
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
                let allAlbums = JSON.parse(req.responseText);
                console.log(allAlbums);

                let username = sessionStorage.getItem('username');

                //filtra album personali e degli altri utenti
                let userAlbums = allAlbums.filter(album => album.username === username);
                let otherAlbums = allAlbums.filter(album => album.username !== username);

                userAlbumsTable.show(userAlbums);
                othersAlbumsTable.show(otherAlbums);
            } else {
                console.error(req.responseText);
            }
        }
    }

    function addAlbum(request) {
        if (request.readyState === XMLHttpRequest.DONE) {
            if (request.status === 200) {
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
                                images = JSON.parse(req.responseText);  //Prendo tutte le immagini
                                document.getElementById("albumTile").textContent = newAlbum.title;
                                document.getElementById("albumCreator").textContent = newAlbum.authorUsername;
                                document.getElementById("albumDate").textContent = newAlbum.date;
                                currentOffset = 0;
                                displayImages(images);
                            } else {
                                console.error(req.responseText);
                            }
                        }
                    });
                });
                //aggiungi in coda alla lista
                newLinkcell.appendChild(newButton);
                newRow.appendChild(newLinkcell);

                //Aggiorna
                document.getElementById("userAlbumsContainer").appendChild(newRow);
                document.getElementById("userAlbumsContainer").style.display = "block";

                //nascondi form creazione
                document.getElementById('toggle').textContent = "Create new album";
                document.getElementById('createAlbumFormContainer').style.display = "none";
            }
        }
    }

    function insertList(req) {
        if (req.readyState === XMLHttpRequest.DONE) {
            if (req.status === 200) {
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
                console.error(req.responseText);
            }
        }
    }

    function displayImages(images) {
        let albumImagesContainer = document.getElementById("albumImagesContainer");
        let albumImagesDiv = document.getElementById("albumImagesDiv");
        let imageRow = document.getElementById("imageRow");

        albumImagesDiv.style.display = "block";

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
    }

    //Funzioni finestra modale

    var imageModal = document.getElementById("imageModal");
    var modalImage = document.getElementById("modalImage");
    var modalTitle = document.getElementById("modalTitle");
    var modalAuthor = document.getElementById("modalAuthor");
    var modalDate = document.getElementById("modalDate");
    var span = document.getElementsByClassName("close")[0];

    function showModal(image) {
        modalImage.src = image.path;
        modalTitle.textContent = image.title;
        modalAuthor.textContent = image.idAuthor;
        modalDate.textContent = image.date;

        imageModal.style.display = "block";
    }

    //Viene chiusa se si clicca la X o si esce con il mouse
    function hideModal() {
        imageModal.style.display = "none";
    }

    span.onclick = function() {
        hideModal();
    }

    window.onclick = function(event) {
        if (event.target == imageModal) {
            hideModal();
        }
    }

})();
