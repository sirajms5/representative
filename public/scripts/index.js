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

        const representativeDetailsWithImageDiv = document.createElement("div");
        representativeDetailsWithImageDiv.className = "representative-details-with-image-container";
        const representativeMainDetailsWithImage = document.createElement("div");
        representativeMainDetailsWithImage.className = "representative-main-details-with-image";
        const representativeImageContrainer = document.createElement("div");
        representativeImageContrainer.className = "representative-image-container";

        // representative image
        const representativeImg = document.createElement("img");
        representativeImg.className = "representative-image";
        representativeImg.src = representative["photo_url"];
        representativeImg.alt = `Photo of ${representativeName}`;
        representativeImageContrainer.appendChild(representativeImg);
        representativeMainDetailsWithImage.appendChild(representativeImageContrainer);

        const representativeMainDetails = document.createElement("div");
        representativeMainDetails.className = "representative-main-details-container";

        // representative name
        const representativeNamecontrainer = document.createElement("div");
        representativeNamecontrainer.className = "representative-name-container";
        const representativeNameElement = document.createElement("h3");
        representativeNameElement.className = "representative-name";
        const representativeAnchor = document.createElement("a");        
        representativeAnchor.href = representative["url"];
        representativeAnchor.target = "_blank";
        representativeAnchor.className = "representative-anchor";
        representativeAnchor.innerText = representativeName;
        representativeNameElement.appendChild(representativeAnchor);
        representativeNamecontrainer.appendChild(representativeNameElement);
        representativeMainDetails.appendChild(representativeNamecontrainer);        

        const representativeMainDetailsOthers = document.createElement("div");
        representativeMainDetailsOthers.className = "representative-main-details-others-container";

        // political affilitation
        const partiesColorHex = {
            "Conservative": "#002395",
            "Liberal": "#D71920",
            "Bloc Québécois": "#0088CE",
            "NDP": "#FF5800",
            "Independent": "silver",
            "Green Party": "#427730"
        };

        const politicalAffiliation = representative["political_affiliation"];
        const pAContrainer = document.createElement("div");
        pAContrainer.className = "representative-pa-contrainer";
        pAContrainer.style.borderBottomColor = partiesColorHex[politicalAffiliation];
        const pALabel = document.createElement("p");
        pALabel.className = "representative-detail-label";
        pALabel.innerText = "Political affiliation:"
        pAContrainer.appendChild(pALabel);
        const representativePA = document.createElement("p");
        representativePA.className = "representative-pa";
        representativePA.innerText = politicalAffiliation;
        pAContrainer.appendChild(representativePA);
        representativeMainDetailsOthers.appendChild(pAContrainer);

        // constituency
        const constituencyContrainer = document.createElement("div");
        constituencyContrainer.className = "representative-constituency-contrainer";
        const constituencyLabel = document.createElement("p");
        constituencyLabel.className = "representative-detail-label";
        constituencyLabel.innerText = "Constituency:"
        constituencyContrainer.appendChild(constituencyLabel);
        const representativeConstituency = document.createElement("p");
        representativeConstituency.className = "representative-constituency";
        representativeConstituency.innerText = representative["constituency"];
        constituencyContrainer.appendChild(representativeConstituency);
        representativeMainDetailsOthers.appendChild(constituencyContrainer);

        // province or territory
        const provinceContrainer = document.createElement("div");
        provinceContrainer.className = "representative-province-contrainer";
        const provinceLabel = document.createElement("p");
        provinceLabel.className = "representative-detail-label";
        provinceLabel.innerText = "Province / Territory:"
        provinceContrainer.appendChild(provinceLabel);
        const representativeProvince = document.createElement("p");
        representativeProvince.className = "representative-province";
        representativeProvince.innerText = representative["province_or_territory"];
        provinceContrainer.appendChild(representativeProvince);
        representativeMainDetailsOthers.appendChild(provinceContrainer);

        // position
        const positionContrainer = document.createElement("div");
        positionContrainer.className = "representative-position-contrainer";
        const positionLabel = document.createElement("p");
        positionLabel.className = "representative-detail-label";
        positionLabel.innerText = "Position:"
        positionContrainer.appendChild(positionLabel);
        const representativePosition = document.createElement("p");
        representativePosition.className = "representative-position";
        representativePosition.innerText = representative["position"];
        positionContrainer.appendChild(representativePosition);
        representativeMainDetailsOthers.appendChild(positionContrainer);

        // languages
        const languageContrainer = document.createElement("div");
        languageContrainer.className = "representative-language-contrainer";
        const languageLabel = document.createElement("p");
        languageLabel.className = "representative-detail-label";
        languageLabel.innerText = "Language:"
        languageContrainer.appendChild(languageLabel);
        const representativeLanguage = document.createElement("p");
        representativeLanguage.className = "representative-language";
        representativeLanguage.innerText = representative["languages"];
        languageContrainer.appendChild(representativeLanguage);
        representativeMainDetailsOthers.appendChild(languageContrainer);

        // email
        const emailContrainer = document.createElement("div");
        emailContrainer.className = "representative-email-contrainer";
        const emailLabel = document.createElement("p");
        emailLabel.className = "representative-detail-label";
        emailLabel.innerText = "Email:"
        emailContrainer.appendChild(emailLabel);
        const representativeEmail = document.createElement("a");
        representativeEmail.className = "representative-email";
        representativeEmail.href = `mailto:${representative["email"]}`;
        representativeEmail.innerText = representative["email"];
        emailContrainer.appendChild(representativeEmail);
        representativeMainDetailsOthers.appendChild(emailContrainer);

        representativeMainDetails.appendChild(representativeMainDetailsOthers);
        representativeMainDetailsWithImage.appendChild(representativeMainDetails);
        representativeDetailsWithImageDiv.appendChild(representativeMainDetailsWithImage);
        
        // offices
        const representativeOfficeDetailsDiv = document.createElement("div");
        representativeOfficeDetailsDiv.className = "representative-offices-contrainer";

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

        representativeOfficeDetailsDiv.appendChild(officesList);
        // representativeMainDetailsWithOffice.appendChild(representativeOfficeDetailsDiv);
        representativeDetailsWithImageDiv.appendChild(representativeOfficeDetailsDiv);
        representativeArticle.appendChild(representativeDetailsWithImageDiv);
        representativeLi.appendChild(representativeArticle);
        representativesUl.appendChild(representativeLi);
    }

    representativesSection.appendChild(representativesUl);
}
