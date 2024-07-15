(function () {

    // page components
    var userAlbumsTable, othersAlbumsTable, pageOrchestrator = new PageOrchestrator();
    var presentation;

    window.addEventListener("load", () => {
        pageOrchestrator.start(); // initialize the components
        pageOrchestrator.refresh(); // display initial content
    }, false);

    function PersonalMessage(_username, messagecontainer) {
        this.username = _username;
        this.show = function () {
            messagecontainer.textContent = this.username;
        }
    }

    function AlbumTable(_listcontainer, _listcontainerbody) {
        this.listcontainer = _listcontainer;
        this.listcontainerbody = _listcontainerbody;

        this.reset = function () {
            this.listcontainer.style.visibility = "hidden";
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
                    linkcell.textContent = album.id;
                    row.appendChild(linkcell);
                    /*
                    anchor = document.createElement("a");
                    let linkText = document.createTextNode("Link");
                    anchor.appendChild(linkText);
                    anchor.setAttribute('albumId', album.id);
                    anchor.addEventListener("click", (e) => {

                        e.preventDefault();
                        currentSet = 1;
                        photoTable.init(e.target.getAttribute("albumId"));  // quando faccio click su un album

                    }, false);
                    anchor.href = "#";

                    linkcell.appendChild(anchor);*/

                    self.listcontainerbody.appendChild(row);
                });
                this.listcontainer.style.visibility = "visible";
            }
        }
    }


    function PageOrchestrator() {
        this.start = function () {

            let username = sessionStorage.getItem('username');
            let message = document.getElementById("id_username");

            let personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
                document.getElementById("id_username"));
            personalMessage.show();

            userAlbumsTable = new AlbumTable(
                document.getElementById("userAlbumsContainer"),
                document.getElementById("userAlbums")
            );

            othersAlbumsTable = new AlbumTable(
                document.getElementById("othersAlbumsContainer"),
                document.getElementById("othersAlbums")
            );

            /*
            albumForm = new AlbumForm(
                document.getElementById("createAlbumForm"),
                document.getElementById("createAlbumFormContainer")
            );*/

            document.getElementById('toggle').addEventListener('click', () => {
                //albumForm.show();
            });

        };

        this.refresh = function () {
            userAlbumsTable.reset();
            othersAlbumsTable.reset();

            makeCall("GET", "home", null, function (req) {
                if (req.readyState === XMLHttpRequest.DONE) {
                    if (req.status === 200) {
                        let allAlbums = JSON.parse(req.responseText);
                        console.log(allAlbums);

                        let username = sessionStorage.getItem('username');

                        let userAlbums = allAlbums.filter(album => album.username === username);
                        let otherAlbums = allAlbums.filter(album => album.username !== username);

                        userAlbumsTable.show(userAlbums);
                        othersAlbumsTable.show(otherAlbums);
                    } else {
                        console.error(req.responseText);
                    }
                }
            });
        };
    }
})();
