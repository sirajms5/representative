const submitAddressButton = document.getElementById("address-submit-button");
const addressInputField = document.getElementById("address-input-field");

submitAddressButton.addEventListener("click", (event) => {
    event.preventDefault();
    const addressValue = addressInputField.value.trim();

    const { isPostalCode, isLongitudeLatitude, latitude, longitude } = validateAddress(addressValue);

    if (isPostalCode || isLongitudeLatitude) {
        const xmlHttpRequestFetchAddress = new XMLHttpRequest();
        xmlHttpRequestFetchAddress.open("POST", "./private/php/fetch-address.php", true);
        xmlHttpRequestFetchAddress.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        
        xmlHttpRequestFetchAddress.onload = () => {
            if (xmlHttpRequestFetchAddress.status === 200) {
                console.log("Address submitted successfully");
                console.log(xmlHttpRequestFetchAddress.responseText);
                const response = JSON.parse(xmlHttpRequestFetchAddress.responseText);
                console.log(response);
            } else {
                console.error("Failed to connect to fetch-address.php");
            }
        };

        let params = "";
        if (isPostalCode) {
            const sanitizedPostalCode = addressValue.toUpperCase().replace(/\s+/g, "").replace(/(.{3})(.{3})/, "$1 $2");
            params = `postal_code=${encodeURIComponent(sanitizedPostalCode)}&latitude=&longitude=`;
        } else if (isLongitudeLatitude) {
            params = `postal_code=&latitude=${encodeURIComponent(latitude)}&longitude=${encodeURIComponent(longitude)}`;
        }

        xmlHttpRequestFetchAddress.send(params);
    } else {
        console.log("Invalid address provided.");
        // TODO: feedback in the UI
    }
});

function validateAddress(addressValue) {
    const postalCodeRegex = /^[A-Za-z]\d[A-Za-z]\s?\d[A-Za-z]\d$/;
    const coordinateRegex = /^-?\d+(\.\d+)?,\s?-?\d+(\.\d+)?$/;

    if (postalCodeRegex.test(addressValue)) {
        console.log("Valid postal code");
        return { isPostalCode: true, isLongitudeLatitude: false, latitude: null, longitude: null };
    } else if (coordinateRegex.test(addressValue)) {
        const [latitude, longitude] = addressValue.split(',').map(coord => parseFloat(coord.trim()));

        if (latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180) {
            console.log("Valid coordinates");
            return { isPostalCode: false, isLongitudeLatitude: true, latitude, longitude };
        } else {
            console.log("Invalid coordinates range");
            return { isPostalCode: false, isLongitudeLatitude: false, latitude: null, longitude: null };
        }
    } else {
        console.log("Invalid address");
        return { isPostalCode: false, isLongitudeLatitude: false, latitude: null, longitude: null };
    }
}
