const api_id = "$apiId", api_code = "$apiCode"; // Replace this values with your UNNAX API data
const autoSubmit = (data) => {

    const width = 500;
    const height = 500;

    const iframe = document.createElement('iframe');

    iframe.setAttribute('name', 'frame');

    iframe.frameBorder = '0';
    iframe.style.width = `${width}px`;
    iframe.style.height = `${height}px`;

    let form = document.createElement("form");
    document.body.appendChild(form);
    form.method = "POST";
    form.action = data.action;
    form.target = data.target;
    form.id = "unnax_frm";

    Object.keys(data).forEach((key) => {
        let el = document.createElement("INPUT");
        el.name = key;
        el.value = data[key];
        el.type = 'hidden'
        form.appendChild(el);
    });

    const popup = createPopup(iframe, width, height);
    document.body.insertAdjacentElement('beforeend', popup);

    form.submit();
}

function createPopup(content, width, heigth) {

    const overlay = document.createElement('div');
    overlay.classList.add('popup-overlay');
    overlay.style.cssText = `
        position: absolute;
        z-index: 9000;
        left: 0;
        right: 0;
        bottom: 0;
        top: 0;
        display: flex;
        justify-content: center;
        align-items: center;
        background: #ffffffa6;
    `;

    const popup = document.createElement('div');
    popup.style.cssText = `
        padding: 10px;
        width: ${width}px;
        background: white;
        -webkit-box-shadow: 4px 3px 5px 0px #cccccc;
        box-shadow: 4px 3px 15px 0px #cccccc;
    `;

    const popupHeader = document.createElement('div');
    popupHeader.style.cssText = `
        display: flex;
        justify-content: flex-end;
    `;

    const closeButton = createCloseButton();
    popupHeader.insertAdjacentElement('beforeend', closeButton);

    popup.insertAdjacentElement('beforeend', popupHeader);
    popup.insertAdjacentElement('beforeend', content);
    overlay.insertAdjacentElement('beforeend', popup);

    return overlay;


    function createCloseButton() {
        const closeButton = document.createElement('div');
        closeButton.style.cssText = `
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            width: 20px;
            height: 20px;
            cursor: pointer;
        `;

        const backgroundColor = '#6d6d6d';
        const width = '2px';
        const height = '20px';

        const leftXPart = document.createElement('div');
        leftXPart.style.cssText = `
            position: absolute;
            width: ${width};
            height: ${height};
            background: ${backgroundColor};
            transform: rotate(45deg);
        `;

        const rightXPart = document.createElement('div');

        rightXPart.style.cssText = `
            position: absolute;
            width: ${width};
            height: ${height};
            background: ${backgroundColor};
            transform: rotate(-45deg);
        `;

        closeButton.insertAdjacentElement('beforeend', leftXPart);
        closeButton.insertAdjacentElement('beforeend', rightXPart);

        closeButton.addEventListener('click', function closePopup() {
            this.removeEventListener('click', closePopup);
            this.closest('.popup-overlay').remove();
        });

        return closeButton;
    }
}

const data = {
    "merchant_id": api_id,
    "has_header": true,
    "has_logo": true,
    "url_logo": "https://s3.us-east-2.amazonaws.com/assets.unnax.com/logos/unnax-logo.jpg",
    "concept": "Unnax V3 Card Preauthotize",
     "order_code": "$clientNumber",
    "target": "frame",
    "action": "https://integration.unnax.com/api/v3/payment/creditcard/preauthorize/"
}

data.merchant_signature = sha1(data.order_code + api_code);

autoSubmit(data);
