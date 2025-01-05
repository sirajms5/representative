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
                console.log("Address submitted successfully");
                console.log(xmlHttpRequestFetchAddress.responseText);
                const response = JSON.parse(xmlHttpRequestFetchAddress.responseText);
                console.log(response);
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
        let representativeName;
        if(representative["is_honourable"]) {
            representativeName = `The Honourable ${representative["first_name"]} ${representative["last_name"]}`;
        } else {
            representativeName = `${representative["first_name"]} ${representative["last_name"]}`;
        }
        
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
        const representativeImagecontainer = document.createElement("div");
        representativeImagecontainer.className = "representative-image-container";

        // representative image
        const representativeImg = document.createElement("img");
        representativeImg.className = "representative-image";
        representativeImg.src = representative["photo_url"];
        representativeImg.alt = `Photo of ${representativeName}`;
        representativeImagecontainer.appendChild(representativeImg);
        representativeMainDetailsWithImage.appendChild(representativeImagecontainer);

        const representativeMainDetails = document.createElement("div");
        representativeMainDetails.className = "representative-main-details-container";

        // representative name
        const representativeNamecontainer = document.createElement("div");
        representativeNamecontainer.className = "representative-name-container";
        const representativeNameElement = document.createElement("h3");
        representativeNameElement.className = "representative-name";
        const representativeAnchor = document.createElement("a");        
        representativeAnchor.href = representative["url"];
        representativeAnchor.target = "_blank";
        representativeAnchor.className = "representative-anchor";
        representativeAnchor.innerText = representativeName;
        representativeNameElement.appendChild(representativeAnchor);
        representativeNamecontainer.appendChild(representativeNameElement);
        representativeMainDetails.appendChild(representativeNamecontainer);        

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
        const pAcontainer = document.createElement("div");
        pAcontainer.className = "representative-pa-container";
        pAcontainer.style.borderBottomColor = partiesColorHex[politicalAffiliation];
        const pALabel = document.createElement("p");
        pALabel.className = "representative-detail-label labelAndValue";
        pALabel.innerText = "Political affiliation:"
        pAcontainer.appendChild(pALabel);
        const representativePA = document.createElement("p");
        representativePA.className = "representative-pa labelAndValue";
        representativePA.innerText = politicalAffiliation;
        pAcontainer.appendChild(representativePA);
        representativeMainDetailsOthers.appendChild(pAcontainer);

        // constituency
        const constituencycontainer = document.createElement("div");
        constituencycontainer.className = "representative-constituency-container";
        const constituencyLabel = document.createElement("p");
        constituencyLabel.className = "representative-detail-label labelAndValue";
        constituencyLabel.innerText = "Constituency:"
        constituencycontainer.appendChild(constituencyLabel);
        const representativeConstituency = document.createElement("p");
        representativeConstituency.className = "representative-constituency labelAndValue";
        representativeConstituency.innerText = representative["constituency"];
        constituencycontainer.appendChild(representativeConstituency);
        representativeMainDetailsOthers.appendChild(constituencycontainer);

        // province or territory
        const provincecontainer = document.createElement("div");
        provincecontainer.className = "representative-province-container";
        const provinceLabel = document.createElement("p");
        provinceLabel.className = "representative-detail-label labelAndValue";
        provinceLabel.innerText = "Province / Territory:"
        provincecontainer.appendChild(provinceLabel);
        const representativeProvince = document.createElement("p");
        representativeProvince.className = "representative-province labelAndValue";
        representativeProvince.innerText = representative["province_or_territory"];
        provincecontainer.appendChild(representativeProvince);
        representativeMainDetailsOthers.appendChild(provincecontainer);

        // position
        const positioncontainer = document.createElement("div");
        positioncontainer.className = "representative-position-container";
        const positionLabel = document.createElement("p");
        positionLabel.className = "representative-detail-label labelAndValue";
        positionLabel.innerText = "Position:"
        positioncontainer.appendChild(positionLabel);
        const representativePosition = document.createElement("p");
        representativePosition.className = "representative-position labelAndValue";
        representativePosition.innerText = representative["position"];
        positioncontainer.appendChild(representativePosition);
        representativeMainDetailsOthers.appendChild(positioncontainer);

        // languages
        const languagecontainer = document.createElement("div");
        languagecontainer.className = "representative-language-container";
        const languageLabel = document.createElement("p");
        languageLabel.className = "representative-detail-label labelAndValue";
        languageLabel.innerText = "Language:"
        languagecontainer.appendChild(languageLabel);
        const representativeLanguage = document.createElement("p");
        representativeLanguage.className = "representative-language labelAndValue";
        representativeLanguage.innerText = representative["languages"];
        languagecontainer.appendChild(representativeLanguage);
        representativeMainDetailsOthers.appendChild(languagecontainer);

        // email
        const emailcontainer = document.createElement("div");
        emailcontainer.className = "representative-email-container";
        const emailLabel = document.createElement("p");
        emailLabel.className = "representative-detail-label labelAndValue";
        emailLabel.innerText = "Email:"
        emailcontainer.appendChild(emailLabel);
        const representativeEmail = document.createElement("a");
        representativeEmail.className = "representative-email labelAndValue";
        representativeEmail.href = `mailto:${representative["email"]}`;
        representativeEmail.innerText = representative["email"];
        emailcontainer.appendChild(representativeEmail);
        representativeMainDetailsOthers.appendChild(emailcontainer);

        representativeMainDetails.appendChild(representativeMainDetailsOthers);
        representativeMainDetailsWithImage.appendChild(representativeMainDetails);
        representativeDetailsWithImageDiv.appendChild(representativeMainDetailsWithImage);
        
        // offices
        const officesToggler = document.createElement("div");
        officesToggler.className = "offices-toggler";        
        const representativeOfficeDetailsDiv = document.createElement("div");
        representativeOfficeDetailsDiv.className = "representative-offices-container";

        const representativeLegislatureOfficeContainer = document.createElement("div");
        representativeLegislatureOfficeContainer.className = "representative-legislature-office-container";
        const legislatureOfficesTitle = document.createElement("h4");
        legislatureOfficesTitle.className = "legislature-office-title";
        const legilatureOffices = representative["legislature_offices"];
        if(legilatureOffices.length > 1) {
            legislatureOfficesTitle.innerText = "Legislature offices";
        } else {
            legislatureOfficesTitle.innerText = "Legislature office";
        }

        representativeLegislatureOfficeContainer.appendChild(legislatureOfficesTitle);
        const legislatureOfficesList = document.createElement("ul");
        legislatureOfficesList.className = "legilature-offices-list";
        generateOfficesList(legislatureOfficesList, legilatureOffices);
        representativeLegislatureOfficeContainer.appendChild(legislatureOfficesList);
        representativeOfficeDetailsDiv.appendChild(representativeLegislatureOfficeContainer);


        const representativeConstituencyOfficeContainer = document.createElement("div");
        representativeConstituencyOfficeContainer.className = "representative-constituency-office-container"
        const constituencyOfficesTitle = document.createElement("h4");
        constituencyOfficesTitle.className = "constituency-office-title";
        const constituencyOffices = representative["constituency_offices"];
        if(constituencyOffices.length > 1) {
            constituencyOfficesTitle.innerText = "Constituency offices";
        } else {
            constituencyOfficesTitle.innerText = "Constituency office";
        }

        representativeConstituencyOfficeContainer.appendChild(constituencyOfficesTitle);
        const constituencyOfficesList = document.createElement("ul");
        constituencyOfficesList.className = "constituency-offices-list";
        generateOfficesList(constituencyOfficesList, constituencyOffices);
        representativeConstituencyOfficeContainer.appendChild(constituencyOfficesList);
        representativeOfficeDetailsDiv.appendChild(representativeConstituencyOfficeContainer);
        officesToggler.appendChild(representativeOfficeDetailsDiv);

        // toggler button
        const toggleButtonContainer = document.createElement("div");
        toggleButtonContainer.className = "toggle-button-container";
        const togglebutton = document.createElement("p");
        togglebutton.className = "toggle-button";
        togglebutton.innerText = "Offices";
        const toggleButtonIcon = document.createElement("i");
        toggleButtonIcon.className = "fas fa-chevron-down";
        toggleButtonContainer.addEventListener('click', function () {
            const container = document.querySelector('.representative-offices-container');
            container.classList.toggle('visible');
            if (container.classList.contains('visible')) {
                toggleButtonIcon.className = "fas fa-chevron-up";
            } else {
                toggleButtonIcon.className = "fas fa-chevron-down";
            }
        });
        
        toggleButtonContainer.appendChild(togglebutton);
        toggleButtonContainer.appendChild(toggleButtonIcon);
        officesToggler.appendChild(toggleButtonContainer);        
        representativeDetailsWithImageDiv.appendChild(officesToggler);
        representativeArticle.appendChild(representativeDetailsWithImageDiv);
        representativeLi.appendChild(representativeArticle);
        representativesUl.appendChild(representativeLi);
    }

    representativesSection.appendChild(representativesUl);
}

function generateOfficesList(parentListNode, list) {
    for(let innerIndex = 0; innerIndex < list.length; innerIndex++) {
        const officeLi = document.createElement("li");
        officeLi.className = "office-list-item";

        // office address - postal code
        const addresscontainer = document.createElement("div");
        addresscontainer.className = "representative-office-address-container";
        const addressLabel = document.createElement("p");
        addressLabel.className = "representative-detail-label labelAndValue";
        addressLabel.innerText = "Address:"
        addresscontainer.appendChild(addressLabel);
        const officeAddress = document.createElement("address");
        officeAddress.className = "office-address labelAndValue";
        officeAddress.innerText = list[innerIndex]["postal_code"];
        addresscontainer.appendChild(officeAddress);
        officeLi.appendChild(addresscontainer);

        // office phone
        const telephonecontainer = document.createElement("div");
        telephonecontainer.className = "representative-office-telephone-container";
        const telephoneLabel = document.createElement("p");
        telephoneLabel.className = "representative-detail-label labelAndValue";
        telephoneLabel.innerText = "Telephone:"
        telephonecontainer.appendChild(telephoneLabel);
        const phoneNumber = list[innerIndex]["phone"];
        const officePhone = document.createElement("p");
        officePhone.className = "office-phone labelAndValue";
        if(phoneNumber !== "") {
            const officePhoneAnchor = document.createElement("a");
            officePhoneAnchor.className = "office-phone-anchor labelAndValue";
            officePhoneAnchor.href = `tel:+${phoneNumber}`;
            officePhoneAnchor.innerText = phoneNumber;
            officePhone.appendChild(officePhoneAnchor);            
        } else {
            const officePhoneElement = document.createElement("p");
            officePhoneElement.className = "office-phone-p labelAndValue";
            officePhoneElement.innerText = "N/A";
            officePhone.appendChild(officePhoneElement);
        }
        
        telephonecontainer.appendChild(officePhone);
        officeLi.appendChild(telephonecontainer);

        // office fax
        const faxcontainer = document.createElement("div");
        faxcontainer.className = "representative-office-fax-container";
        const faxLabel = document.createElement("p");
        faxLabel.className = "representative-detail-label labelAndValue";
        faxLabel.innerText = "Fax:"
        faxcontainer.appendChild(faxLabel);
        const faxNumber = list[innerIndex]["fax"];
        const officeFax = document.createElement("P");
        officeFax.className = "office-fax labelAndValue";
        if(faxNumber !== "") {
            const officeFaxAnchor = document.createElement("a");
            officeFaxAnchor.className = "office-fax-anchor labelAndValue";
            officeFaxAnchor.href = `tel:+${faxNumber}`;
            officeFaxAnchor.innerText = faxNumber;
            officeFax.appendChild(officeFaxAnchor);
        } else {
            const officeFaxElement = document.createElement("p");
            officeFaxElement.className = "office-fax-p labelAndValue";
            officeFaxElement.innerText = "N/A";
            officeFax.appendChild(officeFaxElement);
        }        
        
        faxcontainer.appendChild(officeFax);
        officeLi.appendChild(faxcontainer);

        parentListNode.appendChild(officeLi);
    }
}
