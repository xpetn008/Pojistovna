var closeButton = document.querySelector('.close-button');
var alertBox = document.querySelector('.outerWrapper');

//Alert message close function
closeButton.addEventListener('click', function() {
    alertBox.style.display = 'none';
});
