document.addEventListener("DOMContentLoaded", () => {
    const breadcrumbContainer = document.getElementById('breadcrumbContainer');
    const roleElement = document.getElementById('role');
    if (!breadcrumbContainer && !roleElement) return;

    const path = breadcrumbContainer.getAttribute('data-page');
    let role = roleElement.value;

    if (!path || !role) return;

    role = role.replace(/_/g, '-');
    const segments = path.split(',').map(item => {
        let [label, href] = item.split(':');
        if (href && href.toLowerCase().includes('role')) {
            href = role ? href.replace('role', role) : href;
        }
        return { label, href };
    });

    const breadcrumbHtml = segments.map((segment, index) => {
        const displayName = segment.label.replace(/[-_]/g, ' ').replace(/\b\w/g, char => char.toUpperCase());

        return index === segments.length - 1
            ? `<li>${displayName}</li>`
            : `<li><a href="${segment.href}">${displayName}</a></li>`;
    }).join('');

    breadcrumbContainer.innerHTML = `<ul class="breadcrumb">${breadcrumbHtml}</ul>`;
});