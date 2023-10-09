const selectElement = document.getElementById("selectElement");
const inputElement = document.getElementById("inputElement");
const jmenoElement = document.getElementById("spanJmeno");
const prijmeniElement = document.getElementById("spanPrijmeni");
const telefonElement = document.getElementById("spanTelefon");
const uliceCpElement = document.getElementById("spanUliceCp");
const mestoElement = document.getElementById("spanMesto");
const pscElement = document.getElementById("spanPsc");

document.addEventListener("DOMContentLoaded", function() {
    let selectedValue = selectElement.value;

    if (selectedValue === "jmeno") {
        inputElement.placeholder = jmenoElement.textContent;
    } else if (selectedValue === "prijmeni") {
        inputElement.placeholder = prijmeniElement.textContent;
    } else if (selectedValue === "telefon") {
        inputElement.placeholder = telefonElement.textContent;
    } else if (selectedValue === "uliceCp") {
        inputElement.placeholder = uliceCpElement.textContent;
    } else if (selectedValue === "mesto") {
        inputElement.placeholder = mestoElement.textContent;
    } else if (selectedValue === "psc") {
        inputElement.placeholder = pscElement.textContent;
    }
    inputElement.value = "";

    selectElement.addEventListener("change", function() {
        selectedValue = selectElement.value;

        if (selectedValue === "jmeno") {
            inputElement.placeholder = jmenoElement.textContent;
        } else if (selectedValue === "prijmeni") {
            inputElement.placeholder = prijmeniElement.textContent;
        } else if (selectedValue === "telefon") {
            inputElement.placeholder = telefonElement.textContent;
        } else if (selectedValue === "uliceCp") {
            inputElement.placeholder = uliceCpElement.textContent;
        } else if (selectedValue === "mesto") {
            inputElement.placeholder = mestoElement.textContent;
        } else if (selectedValue === "psc") {
            inputElement.placeholder = pscElement.textContent;
        }
        inputElement.value = "";

    });
});