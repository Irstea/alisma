#!/bin/bash
pause()
{
echo "Appuyez sur Entrée pour continuer..."
read
}
echo "Attention, l'extraction ne fonctionne pas si le poste de travail est situe derriere un serveur proxy"
echo "Veuillez patienter, l'extraction est longue..."
cd extract_stations_from_sandre
# call extract_stations_from_sandre_run.bat --context_param inifilename="../param/param.ini"
./extract_stations_from_sandre_run.sh --context_param inifilename="../../param/param.ini"
echo "Extraction terminée"
cd ..
pause

