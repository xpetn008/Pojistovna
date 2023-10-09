const mesicniSplatkaElement = document.getElementById("spanMesicniSplatka");
const maximalniCastkaElement = document.getElementById("spanMaximalniCastka");
const selectElement = document.getElementById("selectElement");
const inputElement = document.getElementById("inputElement");

document.addEventListener("DOMContentLoaded", function() {
    let selectedValue = selectElement.value;

    if (selectedValue === "mesicni_splatka"){
        inputElement.placeholder = mesicniSplatkaElement.textContent;
    } else if (selectedValue === "maximalni_castka"){
        inputElement.placeholder = maximalniCastkaElement.textContent;
    }
    inputElement.value = "";

    selectElement.addEventListener("change", function() {
        selectedValue = selectElement.value;

        if (selectedValue === "mesicni_splatka"){
            inputElement.placeholder = mesicniSplatkaElement.textContent;
        } else if (selectedValue === "maximalni_castka"){
            inputElement.placeholder = maximalniCastkaElement.textContent;
        }
        inputElement.value = "";

    });
});