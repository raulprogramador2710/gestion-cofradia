document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('confirmModal');
    if (!modal) return;

    const btnAccept = document.getElementById('confirmAccept');
    const btnCancel = document.getElementById('confirmCancel');
    const modalTitle = document.getElementById('confirmModalTitle');
    const modalDesc = document.getElementById('confirmModalDesc');
    
    let targetForm = null;

    document.body.addEventListener('click', function (e) {
        const btn = e.target.closest('.btn-confirm-delete');
        if (!btn) return;

        e.preventDefault();
        targetForm = btn.closest('form');

        // Personalizar textos si el botón tiene los data-attributes
        if(btn.dataset.confirmTitle) modalTitle.textContent = btn.dataset.confirmTitle;
        if(btn.dataset.confirmMessage) modalDesc.textContent = btn.dataset.confirmMessage;
        if(btn.dataset.confirmAccept) btnAccept.textContent = btn.dataset.confirmAccept;

        // MOSTRAR MODAL
        modal.setAttribute('aria-hidden', 'false');
    });

    // CERRAR MODAL
    const closeModal = () => modal.setAttribute('aria-hidden', 'true');
    
    btnCancel.addEventListener('click', closeModal);
    document.querySelectorAll('[data-close-modal]').forEach(el => el.addEventListener('click', closeModal));

    btnAccept.addEventListener('click', () => {
        if (targetForm) targetForm.submit();
    });
});