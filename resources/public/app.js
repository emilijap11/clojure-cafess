// Primer podataka, kasnije možeš ih uzeti iz backend-a
const cafes = [
    {name: "Kafeterija", location: "Dorćol", lat: 44.8186, lng: 20.4600, milk: ["soy","cow"]},
    {name: "Pržionica D59B", location: "Vračar", lat: 44.8100, lng: 20.4750, milk: ["cow","coconut"]},
    {name: "Blaznavac Café", location: "Centar", lat: 44.8170, lng: 20.4570, milk: ["soy","cow","coconut"]}
];

// Distance function (copy from clojure)
function distanceKm(lat1, lon1, lat2, lon2) {
    const R = 6371; // km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
        Math.sin(dLon/2) * Math.sin(dLon/2);
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
}

// Funkcija za render liste kafica
function renderList(list) {
    const ul = document.getElementById('cafes-list');
    ul.innerHTML = '';
    list.forEach(cafe => {
        const li = document.createElement('li');
        li.textContent = `${cafe.name} - ${cafe.location}`;
        ul.appendChild(li);
    });
}

// Filtriranje po lokaciji i udaljenosti
function filterCafes() {
    const loc = document.getElementById('location-filter').value;
    const maxDist = parseFloat(document.getElementById('distance-filter').value);

    // Pretpostavimo da je korisnik na nekoj lokaciji (primer)
    const userLat = 44.8170;
    const userLng = 20.4600;

    let filtered = cafes;

    if (loc) filtered = filtered.filter(c => c.location === loc);
    if (!isNaN(maxDist)) {
        filtered = filtered.filter(c => distanceKm(userLat, userLng, c.lat, c.lng) <= maxDist);
    }

    renderList(filtered);
}


document.getElementById('location-filter').addEventListener('change', filterCafes);
document.getElementById('distance-filter').addEventListener('input', filterCafes);


renderList(cafes);
