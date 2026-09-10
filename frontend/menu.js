
//console.log('probando 123');
const platos =[
{id: 1, nombre:'Milanesa con pure', descripcion:'Clasica, con guarnicion',categoria:'Clasico',precio:800},
    {id: 2, nombre:'Pastel de papa', descripcion:'Clasico sin aceitunas',categoria:'Clasico',precio:850},
    {id: 3, nombre:'Tortilla de acelga', descripcion:'Clasica',categoria:'Vegetarianos',precio:900},
    {id: 4, nombre:'Matambre de cerdo ', descripcion:'Con ensalda o papas fritas',categoria:'Clasico',precio:1000},
    ];

console.log('cantidad platos',platos.length)
function crearTarjeta(plato){
return `
<article class="card-dia">
<h3> ${plato.nombre}</h3>
<p class="opcion"><em>${plato.descripcion}</em></p>
<p class="opcion">${plato.categoria}</p>
<p class="plazo">${plato.precio}</p>
</article>
`}

function renderPlatos(lista){
    const contener = document.querySelector('#lista-platos');
    let html = '';
    for (const plato of lista){
        html = html + crearTarjeta(plato);
    }
    contener.innerHTML = html;
}
renderPlatos(platos);

