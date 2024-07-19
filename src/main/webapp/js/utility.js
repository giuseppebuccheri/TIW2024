function makeGet(url, callbackFunction) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = () => callbackFunction(req);
    req.open("GET", url);
    req.send();
}

function makePost(url, formElement, callbackFunction) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = () => callbackFunction(req);
    req.open("POST", url);
    req.send(formElement);
}

function makePut(url, formElement, callbackFunction) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = () => callbackFunction(req);
    req.open("PUT", url);
    req.send(formElement);
}

function makeDelete(url, callbackFunction) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = () => callbackFunction(req);
    req.open("DELETE", url);
    req.send();
}