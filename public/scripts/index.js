const submitAddressButton = document.getElementById("address-submit-button");
const addressInputField = document.getElementById("address-input-field");
const representativesSection = document.getElementById("representatives-section");

submitAddressButton.addEventListener("click", (event) => {
    event.preventDefault();
    representativesSection.replaceChildren();    
    const addressValue = addressInputField.value.trim();
    const { isPostalCode, isLongitudeLatitude, latitude, longitude } = validateAddress(addressValue);
    if (isPostalCode || isLongitudeLatitude) {
        const xmlHttpRequestFetchAddress = new XMLHttpRequest();
        xmlHttpRequestFetchAddress.open("POST", "./private/php/fetch-address.php", true);
        xmlHttpRequestFetchAddress.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        
        xmlHttpRequestFetchAddress.onload = () => {
            if (xmlHttpRequestFetchAddress.status === 200) {
                // console.log("Address submitted successfully");
                // console.log(xmlHttpRequestFetchAddress.responseText);
                const response = JSON.parse(xmlHttpRequestFetchAddress.responseText);
                // console.log(response);
                setupRepresentativesHTML(response);
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

function setupRepresentativesHTML(representativesJson) {
    const representativesUl = document.createElement("ul");
    representativesUl.id = "representatives-list";
    console.log(representativesJson.length);
    for(let index = 0; index < representativesJson.length; index++) {
        const representative = representativesJson[index];
        const representativeName = `${representative["first_name"]} ${representative["last_name"]}`;
        const representativeLi = document.createElement("li");
        representativeLi.className = "representative-list-item";
        const representativeArticle = document.createElement("article");
        representativeArticle.className = "representative-article";
        const representativeLevelDiv = document.createElement("div");
        representativeLevelDiv.className = "representative-level-container";

        // level
        const representativeLevel = document.createElement("h2");
        representativeLevel.className = "representative-level";
        representativeLevel.innerText = representative["level"];
        representativeLevelDiv.appendChild(representativeLevel);
        representativeArticle.appendChild(representativeLevelDiv);

        const representativeDetailsDiv = document.createElement("div");
        representativeDetailsDiv.className = "representative-details-container";
        const representativeImageContrainer = document.createElement("div");
        representativeImageContrainer.className = "representative-image-container";

        // representative image
        const representativeImg = document.createElement("img");
        representativeImg.className = "representative-image";
        representativeImg.src = representative["photo_url"];
        representativeImg.alt = `Photo of ${representativeName}`;
        representativeImageContrainer.appendChild(representativeImg);
        representativeDetailsDiv.appendChild(representativeImageContrainer);

        const representativeMainDetails = document.createElement("div");
        representativeMainDetails.className = "representative-main-details-container";

        // representative name
        const representativeNameElement = document.createElement("h3");
        representativeNameElement.className = "representative-name";
        const representativeAnchor = document.createElement("a");        
        representativeAnchor.href = representative["url"];
        representativeAnchor.target = "_blank";
        representativeAnchor.className = "representative-anchor";
        representativeAnchor.innerText = representativeName;
        representativeNameElement.appendChild(representativeAnchor);
        representativeMainDetails.appendChild(representativeNameElement);        

        // political affilitation
        const representativePA = document.createElement("p");
        representativePA.className = "representative-pa";
        representativePA.innerText = representative["political_affiliation"];
        representativeMainDetails.appendChild(representativePA);

        // constituency
        const representativeConstituency = document.createElement("p");
        representativeConstituency.className = "representative-constituency";
        representativeConstituency.innerText = representative["constituency"];
        representativeMainDetails.appendChild(representativeConstituency);

        // province or territory
        const representativeProvince = document.createElement("p");
        representativeProvince.className = "representative-province";
        representativeProvince.innerText = representative["province_or_territory"];
        representativeMainDetails.appendChild(representativeProvince);

        // position
        const representativePosition = document.createElement("p");
        representativePosition.className = "representative-position";
        representativePosition.innerText = representative["position"];
        representativeMainDetails.appendChild(representativePosition);

        // languages
        const representativeLanguage = document.createElement("p");
        representativeLanguage.className = "representative-language";
        representativeLanguage.innerText = representative["languages"];
        representativeMainDetails.appendChild(representativeLanguage);

        // email
        const representativeEmail = document.createElement("a");
        representativeEmail.className = "representative-email";
        representativeEmail.href = `mailto:${representative["email"]}`;
        representativeEmail.innerText = representative["email"];
        representativeMainDetails.appendChild(representativeEmail);

        representativeDetailsDiv.appendChild(representativeMainDetails);

        // offices
        const representativeOtherDetailsDiv = document.createElement("div");
        representativeOtherDetailsDiv.className = "representative-offices-contrainer";

        const officesList = document.createElement("ul");
        officesList.className = "offices-list";
        for(let innerIndex = 0; innerIndex < representative["offices"].length; innerIndex++) {
            const officeLi = document.createElement("li");
            officeLi.className = "office-list-item";

            // office type
            const officeType = document.createElement("p");
            officeType.className = "office-type";
            officeType.innerText = representative["offices"][innerIndex]["type"];
            officeLi.appendChild(officeType);

            // office address - postal code
            const officeAddress = document.createElement("address");
            officeAddress.className = "office-address";
            officeAddress.innerText = representative["offices"][innerIndex]["postal_code"];
            officeLi.appendChild(officeAddress);

            // office phone
            const phoneNumber = representative["offices"][innerIndex]["phone"];
            const officePhone = document.createElement("p");
            officePhone.className = "office-phone";
            const officePhoneAnchor = document.createElement("a");
            officePhoneAnchor.className = "office-phone-anchor";
            officePhoneAnchor.href = `tel:+${phoneNumber}`;
            officePhoneAnchor.innerText = phoneNumber;
            officePhone.appendChild(officePhoneAnchor);
            officeLi.appendChild(officePhone);

            // office fax
            const faxNumber = representative["offices"][innerIndex]["fax"];
            const officeFax = document.createElement("P");
            officeFax.className = "office-fax";
            const officeFaxAnchor = document.createElement("a");
            officeFaxAnchor.className = "office-fax-anchor";
            officeFaxAnchor.href = `tel:+${faxNumber}`;
            officeFaxAnchor.innerText = faxNumber;
            officeFax.appendChild(officeFaxAnchor);
            officeLi.appendChild(officeFax);

            officesList.appendChild(officeLi);
        }

        representativeOtherDetailsDiv.appendChild(officesList);
        representativeDetailsDiv.appendChild(representativeOtherDetailsDiv);
        representativeArticle.appendChild(representativeDetailsDiv);
        representativeLi.appendChild(representativeArticle);
        representativesUl.appendChild(representativeLi);
    }

    representativesSection.appendChild(representativesUl);
}
