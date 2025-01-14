const submitAddressButton = document.getElementById("address-submit-button");
const addressInputField = document.getElementById("address-input-field");
const representativesSection = document.getElementById("representatives-section");
const detailedWrapper = document.createElement("div");
const loadingDiv = document.getElementById("loader");
const invalidPostalCode = document.getElementById("invalid-postal-code-error-message");
const searchForm = document.getElementById("serach-form");
const bodyWrapper = document.getElementById("body-wrapper");
const levels = [
    "federal",
    "provincial",
    "municipal"
];
const partiesColorHex = {
    "Alberta New Democratic Party": "#FF5800",
    "BC Green Party": "#427730", 
    "BC United": "aqua",
    "Bloc Québécois": "#0088CE",
    "Coalition avenir Québec": "blue",
    "Conservative": "#002395",
    "Conservative Party of British Columbia": "#002395",
    "Government Caucus": "#FFD700",
    "Green Party": "#427730",
    "Green Party of Ontario": "#427730",
    "Green Party of Prince Edward Island": "#427730",
    "Indépendant": "silver",
    "Independent": "silver",
    "Independent Liberal": "silver",
    "Liberal": "#D71920",
    "Liberal Party": "#D71920",
    "Liberal Party of Newfoundland and Labrador": "#D71920",
    "Liberal Party of Prince Edward Island": "#D71920",
    "NDP": "#FF5800",
    "New Democratic Party": "#FF5800",
    "New Democratic Party of British Columbia": "#FF5800",
    "New Democratic Party of Manitoba": "#FF5800",
    "New Democratic Party of Newfoundland and Labrador": "#FF5800",
    "New Democratic Party of Ontario": "#FF5800",
    "Nova Scotia Liberal Party": "#D71920",
    "Nova Scotia New Democratic Party": "#FF5800",
    "Ontario Liberal Party": "#D71920",
    "Opposition Caucus": "#FFD700",
    "Parti libéral du Québec": "#D71920",
    "Parti québécois": "#0088CE",
    "Progressive Conservative Association of Nova Scotia": "#002395",
    "Progressive Conservative Party": "#002395",
    "Progressive Conservative Party of Manitoba": "#002395",
    "Progressive Conservative Party of Newfoundland and Labrador": "#002395",
    "Progressive Conservative Party of Ontario": "#002395",
    "Progressive Conservative Party of Prince Edward Island": "#002395",
    "Québec solidaire": "#ff5505",
    "United Conservative Party": "#002395",
    "Yukon Liberal Party": "#D71920",
    "Yukon Party": "#002395"
};


submitAddressButton.addEventListener("click", (event) => {
    event.preventDefault();
    bodyHeightControl(true);
    WaitSearch(true);    
    representativesSection.replaceChildren();    
    const addressValue = addressInputField.value.trim();
    const isPostalCode = validateAddress(addressValue);
    if (isPostalCode) {
        if(searchForm.querySelector("#invalid-postal-code-error-message")) {
            searchForm.removeChild(invalidPostalCode);
        }
        
        const xmlHttpRequestFetchAddress = new XMLHttpRequest();
        xmlHttpRequestFetchAddress.open("POST", "./private/php/fetch-address.php", true);
        xmlHttpRequestFetchAddress.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        
        xmlHttpRequestFetchAddress.onload = () => {
            if (xmlHttpRequestFetchAddress.status === 200) {
                WaitSearch(false);
                const response = JSON.parse(xmlHttpRequestFetchAddress.responseText);
                if(response["error"] !== undefined) {
                    setupPostalCodeNotFound();
                } else { 
                    setupRepresentativesHTML(response);
                }
                
            } else {
                WaitSearch(false);
                console.error("Failed to connect to fetch-address.php");
            }
        };

        let params = "";
        const sanitizedPostalCode = addressValue.toUpperCase().replace(/\s+/g, "").replace(/(.{3})(.{3})/, "$1 $2");
        params = `postal_code=${encodeURIComponent(sanitizedPostalCode)}&latitude=&longitude=`;
        

        xmlHttpRequestFetchAddress.send(params);
    } else {
        WaitSearch(false);
        invalidPostalCode.innerText = "Invalid postal code. Expected format K1A 0A2."
        searchForm.appendChild(invalidPostalCode);

    }
});

function validateAddress(addressValue) {
    const postalCodeRegex = /^[A-Za-z]\d[A-Za-z]\s?\d[A-Za-z]\d$/;

    if (postalCodeRegex.test(addressValue)) {
        return true;
    } else {
        return false;
    }
}

function setupRepresentativesHTML(representativesJson) {
    const representativesUl = document.createElement("ul");
    representativesUl.id = "representatives-list";
    levels.forEach(level => {
        const representative = representativesJson[level][0];
        if(representative == null || representative["first_name"] == undefined) {
            return;
        }

        let representativeName;
        if(representative["is_honourable"]) {
            representativeName = `The Honourable ${representative["first_name"]} ${representative["last_name"]}`;
        } else {
            representativeName = `${representative["first_name"]} ${representative["last_name"]}`;
        }
        
        const representativeLi = document.createElement("li");
        representativeLi.className = "representative-list-item";
        const representativeArticle = document.createElement("article");
        representativeArticle.className = "representative-article animate__animated animate__fadeIn";
        const representativeLevelDiv = document.createElement("div");
        representativeLevelDiv.className = "representative-level-container";

        // level
        const representativeLevel = document.createElement("h2");
        representativeLevel.className = "representative-level";
        representativeLevel.innerText = representative["level"];
        representativeLevelDiv.appendChild(representativeLevel);
        representativeArticle.appendChild(representativeLevelDiv);

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
        representativeNameElement.innerText = representativeName;
        representativeNamecontainer.appendChild(representativeNameElement);
        representativeMainDetails.appendChild(representativeNamecontainer);

        const representativeMainDetailsOthers = document.createElement("div");
        representativeMainDetailsOthers.className = "representative-main-details-others-container";

        // political affiliation
        const politicalAffiliation = representative["political_affiliation"];
        const pAcontainer = document.createElement("div");
        pAcontainer.className = "representative-pa-container";
        const representativePA = document.createElement("p");
        representativePA.className = "representative-pa labelAndValue";
        if(politicalAffiliation !== "") {
            representativePA.innerText = politicalAffiliation;
            pAcontainer.style.borderBottomColor = partiesColorHex[politicalAffiliation];
        } else {
            representativePA.innerText = "-";
            pAcontainer.style.borderBottomColor = "transparent";
        }

        pAcontainer.appendChild(representativePA);
        representativeMainDetailsOthers.appendChild(pAcontainer);

        const constituencyAndProvince = document.createElement("div");
        constituencyAndProvince.className = "constituency-province-wrapper";
        // constituency
        const constituencycontainer = document.createElement("div");
        constituencycontainer.className = "representative-constituency-container";
        const representativeConstituency = document.createElement("p");
        representativeConstituency.className = "representative-constituency labelAndValue";
        representativeConstituency.innerText = representative["constituency"];
        constituencycontainer.appendChild(representativeConstituency);
        // representativeMainDetailsOthers.appendChild(constituencycontainer);
        constituencyAndProvince.appendChild(constituencycontainer);

        // province or territory
        const provincecontainer = document.createElement("div");
        provincecontainer.className = "representative-province-container";
        const representativeProvince = document.createElement("p");
        representativeProvince.className = "representative-province labelAndValue";
        representativeProvince.innerText = representative["province_or_territory"];
        provincecontainer.appendChild(representativeProvince);
        // representativeMainDetailsOthers.appendChild(provincecontainer);
        constituencyAndProvince.appendChild(provincecontainer);
        representativeMainDetailsOthers.appendChild(constituencyAndProvince);

        
        representativeArticle.appendChild(representativeMainDetailsWithImage);
        representativeMainDetails.appendChild(representativeMainDetailsOthers);
        representativeMainDetailsWithImage.appendChild(representativeMainDetails);

        // representative on click data setup
        representativeMainDetailsWithImage.setAttribute("data-photo-url", representative["photo_url"]);   
        representativeMainDetailsWithImage.setAttribute("data-name", representativeName);        
        representativeMainDetailsWithImage.setAttribute("data-source-url", representative["url"]);   
        representativeMainDetailsWithImage.setAttribute("data-political-affiliation", representative["political_affiliation"]);   
        representativeMainDetailsWithImage.setAttribute("data-constituency", representative["constituency"]);   
        representativeMainDetailsWithImage.setAttribute("data-province-territory", representative["province_or_territory"]);   
        representativeMainDetailsWithImage.setAttribute("data-position", representative["position"]);   
        representativeMainDetailsWithImage.setAttribute("data-languages", representative["languages"]);   
        representativeMainDetailsWithImage.setAttribute("data-email", representative["email"]);   
        representativeMainDetailsWithImage.setAttribute("data-legislature-offices", JSON.stringify(representative["legislature_offices"])); 
        representativeMainDetailsWithImage.setAttribute("data-constituency-offices", JSON.stringify(representative["constituency_offices"]));
        
        representativeMainDetailsWithImage.addEventListener("click", (event) => {        
            detailedWrapper.replaceChildren();
            bodyHeightControl(false);
            detailedWrapper.className = "representative-all-details-wrapper";
            const target = event.currentTarget;
            const photoUrl = target.dataset.photoUrl;
            const name = target.dataset.name;
            const sourceUrl = target.dataset.sourceUrl;
            const politicalAffiliation = target.dataset.politicalAffiliation;
            const constituency = target.dataset.constituency;
            const provinceTerritory = target.dataset.provinceTerritory;
            const position = target.dataset.position;
            const languages = target.dataset.languages;
            const email = target.dataset.email;
            const legislatureOffices = JSON.parse(target.dataset.legislatureOffices || "[]");
            const constituencyOffices = JSON.parse(target.dataset.constituencyOffices || "[]");

            // representative image
            const representativeMainDetailsWithImageAllDetails = document.createElement("div");
            representativeMainDetailsWithImageAllDetails.className = "representative-main-details-with-image-all-details animate__animated animate__fadeIn";
            const representativeImagecontainerAllDetails = document.createElement("div");
            representativeImagecontainerAllDetails.className = "representative-image-container-all-details";
            
            const representativeImgAllDetails = document.createElement("img");
            representativeImgAllDetails.className = "representative-image-all-details";
            representativeImgAllDetails.src = photoUrl;
            representativeImgAllDetails.alt = `Photo of ${name}`;
            representativeImagecontainerAllDetails.appendChild(representativeImgAllDetails);
            representativeMainDetailsWithImageAllDetails.appendChild(representativeImagecontainerAllDetails);

            const detailsAndOfficesContainer = document.createElement("div");
            detailsAndOfficesContainer.className = "details-and-offices-container-all-details";
            const representativeMainDetailsAllDetails = document.createElement("div");
            representativeMainDetailsAllDetails.className = "representative-main-details-container";

            // representative name
            const representativeNamecontainerAllDetails = document.createElement("div");
            representativeNamecontainerAllDetails.className = "representative-name-container";
            const representativeNameElementAllDetails = document.createElement("h3");
            representativeNameElementAllDetails.className = "representative-name-all-details";
            if(sourceUrl !== "") {
                const representativeAnchorAllDetails = document.createElement("a");        
                representativeAnchorAllDetails.href = sourceUrl;
                representativeAnchorAllDetails.target = "_blank";
                representativeAnchorAllDetails.className = "representative-anchor";
                representativeAnchorAllDetails.innerText = name;
                representativeNameElementAllDetails.appendChild(representativeAnchorAllDetails);
                representativeNamecontainerAllDetails.appendChild(representativeNameElementAllDetails);
            } else {
                representativeNameElementAllDetails.innerText = name;
                representativeNamecontainerAllDetails.appendChild(representativeNameElementAllDetails);
            }
            
            representativeMainDetailsAllDetails.appendChild(representativeNamecontainerAllDetails); 
            const representativeMainDetailsOthersAllDetails = document.createElement("div");
            representativeMainDetailsOthersAllDetails.className = "representative-main-details-others-container-all-details";

            // political affiliation
            const pAcontainerAllDetails = document.createElement("div");
            pAcontainerAllDetails.className = "representative-pa-container";
            const pALabelAllDetails = document.createElement("p");
            pALabelAllDetails.className = "representative-detail-label labelAndValue";
            pALabelAllDetails.innerText = "Political affiliation:"
            pAcontainerAllDetails.appendChild(pALabelAllDetails);
            const representativePAAllDetails = document.createElement("p");
            representativePAAllDetails.className = "representative-pa labelAndValue";
            if(politicalAffiliation !== "") {
                representativePAAllDetails.innerText = politicalAffiliation;
                pAcontainerAllDetails.style.borderBottomColor = partiesColorHex[politicalAffiliation];
            } else {
                representativePAAllDetails.innerText = "N/A";
                pAcontainerAllDetails.style.borderBottomColor = "transparent";
            }
            
            pAcontainerAllDetails.appendChild(representativePAAllDetails);
            representativeMainDetailsOthersAllDetails.appendChild(pAcontainerAllDetails);

            // constituency
            const constituencycontainerAllDetails = document.createElement("div");
            constituencycontainerAllDetails.className = "representative-constituency-container";
            const constituencyLabelAllDetails = document.createElement("p");
            constituencyLabelAllDetails.className = "representative-detail-label labelAndValue";
            constituencyLabelAllDetails.innerText = "Constituency:"
            constituencycontainerAllDetails.appendChild(constituencyLabelAllDetails);
            const representativeConstituencyAllDetails = document.createElement("p");
            representativeConstituencyAllDetails.className = "representative-constituency labelAndValue";
            representativeConstituencyAllDetails.innerText = constituency;
            constituencycontainerAllDetails.appendChild(representativeConstituencyAllDetails);
            representativeMainDetailsOthersAllDetails.appendChild(constituencycontainerAllDetails);

            // province or territory
            const provincecontainerAllDetails = document.createElement("div");
            provincecontainerAllDetails.className = "representative-province-container";
            const provinceLabelAllDetails = document.createElement("p");
            provinceLabelAllDetails.className = "representative-detail-label labelAndValue";
            provinceLabelAllDetails.innerText = "Province / Territory:"
            provincecontainerAllDetails.appendChild(provinceLabelAllDetails);
            const representativeProvinceAllDetails = document.createElement("p");
            representativeProvinceAllDetails.className = "representative-province labelAndValue";
            representativeProvinceAllDetails.innerText = provinceTerritory;
            provincecontainerAllDetails.appendChild(representativeProvinceAllDetails);
            representativeMainDetailsOthersAllDetails.appendChild(provincecontainerAllDetails);

            // position
            const positioncontainerAllDetails = document.createElement("div");
            positioncontainerAllDetails.className = "representative-position-container";
            const positionLabelAllDetails = document.createElement("p");
            positionLabelAllDetails.className = "representative-detail-label labelAndValue";
            positionLabelAllDetails.innerText = "Position:"
            positioncontainerAllDetails.appendChild(positionLabelAllDetails);
            const representativePositionAllDetails = document.createElement("p");
            representativePositionAllDetails.className = "representative-position labelAndValue";
            representativePositionAllDetails.innerText = position;
            positioncontainerAllDetails.appendChild(representativePositionAllDetails);
            representativeMainDetailsOthersAllDetails.appendChild(positioncontainerAllDetails);

            // languages
            const languagecontainerAllDetails = document.createElement("div");
            languagecontainerAllDetails.className = "representative-language-container";
            const languageLabelAllDetails = document.createElement("p");
            languageLabelAllDetails.className = "representative-detail-label labelAndValue";
            languageLabelAllDetails.innerText = "Language:"
            languagecontainerAllDetails.appendChild(languageLabelAllDetails);
            const representativeLanguageAllDetails = document.createElement("p");
            representativeLanguageAllDetails.className = "representative-language labelAndValue";
            if(languages !== "") {
                representativeLanguageAllDetails.innerText = languages;
            } else {
                representativeLanguageAllDetails.innerText = "N/A"
            }

            languagecontainerAllDetails.appendChild(representativeLanguageAllDetails);
            representativeMainDetailsOthersAllDetails.appendChild(languagecontainerAllDetails);

            // email
            const emailcontainerAllDetails = document.createElement("div");
            emailcontainerAllDetails.className = "representative-email-container";
            const emailLabelAllDetails = document.createElement("p");
            emailLabelAllDetails.className = "representative-detail-label labelAndValue";
            emailLabelAllDetails.innerText = "Email:"
            emailcontainerAllDetails.appendChild(emailLabelAllDetails);
            let representativeEmailAllDetails = ""
            if(email === "N/A") {
                representativeEmailAllDetails = document.createElement("p");
                representativeEmailAllDetails.className = "representative-email labelAndValue";
            } else {
                representativeEmailAllDetails = document.createElement("a");
                representativeEmailAllDetails.className = "representative-email labelAndValue";
                representativeEmailAllDetails.href = `mailto:${email}`;
                representativeEmailAllDetails.innerText = email;
            }
            
            emailcontainerAllDetails.appendChild(representativeEmailAllDetails);
            representativeMainDetailsOthersAllDetails.appendChild(emailcontainerAllDetails);

            representativeMainDetailsAllDetails.appendChild(representativeMainDetailsOthersAllDetails);
            detailsAndOfficesContainer.appendChild(representativeMainDetailsAllDetails);

            // offices
            const representativeOfficeDetailsDivAllDetails = document.createElement("div");
            representativeOfficeDetailsDivAllDetails.className = "representative-offices-container";
            const representativeOfficeTitle = document.createElement("p");
            representativeOfficeTitle.className = "offices-container-title";
            if((legislatureOffices.length > 0 && constituencyOffices.length > 0) || legislatureOffices.length > 1 || constituencyOffices.length > 1) {
                representativeOfficeTitle.innerText = "Offices";
            } else if (legislatureOffices.length > 0 || constituencyOffices.length > 0) {
                representativeOfficeTitle.innerText = "Office";
            }

            representativeOfficeDetailsDivAllDetails.appendChild(representativeOfficeTitle);

            const officesDetailsContainer = document.createElement("div");
            officesDetailsContainer.className = "offices-details-container";

            const representativeLegislatureOfficeContainerAllDetails = document.createElement("div");
            representativeLegislatureOfficeContainerAllDetails.className = "representative-legislature-office-container";
            const representativeConstituencyOfficeContainerAllDetails = document.createElement("div");
            representativeConstituencyOfficeContainerAllDetails.className = "representative-constituency-office-container"
            const legislatureOfficesTitleAllDetails = document.createElement("p");
            legislatureOfficesTitleAllDetails.className = "legislature-office-title";
            if(legislatureOffices.length >= 1) {
                if(legislatureOffices.length > 1) {
                    legislatureOfficesTitleAllDetails.innerText = "Legislature offices";
                } else {
                    legislatureOfficesTitleAllDetails.innerText = "Legislature office";
                }

                representativeLegislatureOfficeContainerAllDetails.appendChild(legislatureOfficesTitleAllDetails);
                const legislatureOfficesListAllDetails = document.createElement("ul");
                legislatureOfficesListAllDetails.className = "legilature-offices-list";
                generateOfficesList(legislatureOfficesListAllDetails, legislatureOffices);
                representativeLegislatureOfficeContainerAllDetails.appendChild(legislatureOfficesListAllDetails);
                officesDetailsContainer.appendChild(representativeLegislatureOfficeContainerAllDetails);
                representativeConstituencyOfficeContainerAllDetails.style.marginLeft = "3rem";
            } else {
                representativeConstituencyOfficeContainerAllDetails.style.marginLeft = "0";
            }
            
            const constituencyOfficesTitleAllDetails = document.createElement("p");
            constituencyOfficesTitleAllDetails.className = "constituency-office-title";
            if(constituencyOffices.length >= 1) {
                if(constituencyOffices.length > 1) {
                    constituencyOfficesTitleAllDetails.innerText = "Constituency offices";
                } else {
                    constituencyOfficesTitleAllDetails.innerText = "Constituency office";
                }

                representativeConstituencyOfficeContainerAllDetails.appendChild(constituencyOfficesTitleAllDetails);
                const constituencyOfficesListAllDetails = document.createElement("ul");
                constituencyOfficesListAllDetails.className = "constituency-offices-list";
                generateOfficesList(constituencyOfficesListAllDetails, constituencyOffices);
                representativeConstituencyOfficeContainerAllDetails.appendChild(constituencyOfficesListAllDetails);
                officesDetailsContainer.appendChild(representativeConstituencyOfficeContainerAllDetails);
            } else {
                representativeLegislatureOfficeContainerAllDetails.style.borderRight = "none";
            }

            representativeOfficeDetailsDivAllDetails.appendChild(officesDetailsContainer);
            detailsAndOfficesContainer.appendChild(representativeOfficeDetailsDivAllDetails);            
            representativeMainDetailsWithImageAllDetails.appendChild(detailsAndOfficesContainer);
            detailedWrapper.appendChild(representativeMainDetailsWithImageAllDetails);
            representativesSection.appendChild(detailedWrapper);            
        });

        representativeLi.appendChild(representativeArticle);
        representativesUl.appendChild(representativeLi);
    });

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

function WaitSearch(isWaiting) {
    if(isWaiting) {
        loadingDiv.style.display = "inline";
        submitAddressButton.disabled = true;
    } else {
        loadingDiv.style.display = "none";
        submitAddressButton.disabled = false;
    }
}

function setupPostalCodeNotFound() {
    const postalCodeNotFound = document.createElement("p");
    postalCodeNotFound.id = "postal-code-not-found";
    postalCodeNotFound.innerText = "Postal code not found.";
    representativesSection.appendChild(postalCodeNotFound);
}

function bodyHeightControl(isViewHeight) {
    if(isViewHeight) {
        bodyWrapper.style.height = "100vh";
    } else {
        bodyWrapper.style.height = "auto";
    }
}