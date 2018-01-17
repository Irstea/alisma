#!/bin/bash
pause()
{
echo "Appuyez sur Entrée pour continuer..."
read
}
cd export_csv_calcul
# call export_csv_calcul_run.bat --context_param inifilename="../param/param.ini"
./export_csv_calcul_run.sh --context_param inifilename="../../param/param.ini"
echo "export terminé"
cd ..
pause

