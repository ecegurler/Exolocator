#{extends 'main.html' /}


<div class="main" id="description">
<br><br>
	#{get 'description' /}
</div>

<div class="sidebar1" id="species_specific_search">
<br><br>
	#{get 'species_search' /}
</div>

<div class="sidebar2" id="human_protein_homolog_search">
	#{get 'human_homolog_search' /}
</div>
