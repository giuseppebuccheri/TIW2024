function makeGet(url, callbackFunction) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = () => callbackFunction(req);       //al cambio di stato delle servlet --> funzione di callback
    req.open("GET", url);
    req.send();     //send senza body perchè i dati inviati sono nell'url
}

function makePost(url, formElement, callbackFunction) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = () => callbackFunction(req);       //al cambio di stato delle servlet --> funzione di callback
    req.open("POST", url);
    req.send(formElement);      //inserisco nel body i campi presi dal form
}