const submitAddressButton = document.getElementById("address-submit-button");
const addressInputField = document.getElementById("address-input-field");
submitAddressButton.addEventListener("click", (event) => {
    event.preventDefault();
    const addressValue = addressInputField.value;
    validateAddress(addressValue);
    xmlHttpRequestFetchAddress = new XMLHttpRequest();
    xmlHttpRequestFetchAddress.open("POST", "./php/fetch-address.php", true);
    xmlHttpRequestFetchAddress.setRequestHeader("Content-Type", "application/x-www-form-urlencoded"); // encoded parameters
    xmlHttpRequestFetchAddress.onload = () => {
        if (xmlHttpRequestFetchAddress.status === 200) {
            

        } else {
            console.error("can't connect to cart-count php");
        }

    }

    let params = "address=" + addressValue; 
    xmlHttpRequestFetchAddress.send(params);
});

function validateAddress() {    
    // TODO: vlidate input if postal code or coordinates
}