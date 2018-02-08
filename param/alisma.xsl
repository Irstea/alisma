<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" indent="yes"/>
  <xsl:template match="operations">
    <fo:root>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4-portrait"
              page-height="29.7cm" page-width="21.0cm" margin="1cm" margin-top="0.5cm" margin-bottom="1cm">
              <fo:region-body margin-top="2.5cm"/>
              <fo:region-before extent="1cm"/>
              <fo:region-after extent="0cm"/>     
        </fo:simple-page-master>
      </fo:layout-master-set>
      
      <fo:page-sequence master-reference="A4-portrait">
         <fo:static-content flow-name="xsl-region-after">
     <fo:block text-align="center"> - <fo:page-number/> -  </fo:block>
    </fo:static-content>
    <fo:static-content flow-name="xsl-region-before">
     <fo:block text-align="center" background-image="param/logo.gif" font-weight="bold" font-style="italic" background-position-horizontal="left" background-repeat="no-repeat" line-height="2.5cm">
 Récapitulatif des relevés - Macrophytes cours d'eau - Alisma v. <xsl:value-of select="versions/softwareVersion"/>
 </fo:block>
 
    </fo:static-content>

        <fo:flow flow-name="xsl-region-body">        
          <fo:block>
          <xsl:apply-templates select="operation" />
          </fo:block>

        </fo:flow>
      </fo:page-sequence>
    </fo:root>
   </xsl:template>
  <xsl:template match="operation">

  <fo:block border="thin" space-before.optimum="1em"/>
    <fo:block>
 Station :&#160;<fo:inline font-weight="bold"> <xsl:value-of select="cd_station"/>&#160;<xsl:value-of select="station"/></fo:inline>
 &#160;Date du relevé : <fo:inline font-weight="bold" > <xsl:value-of select="date_op"/></fo:inline> 
 </fo:block>
 <fo:block>
 Référence du dossier : <fo:inline font-weight="bold" > <xsl:value-of select="ref_dossier"/></fo:inline>
 &#160;Statut : <fo:inline font-style="italic"><xsl:value-of select="libelle_statut"/></fo:inline></fo:block>
 <fo:block>Identifiant unique (UUID) : <xsl:value-of select="uuid"/></fo:block>
  <fo:block>
  Organisme :&#160; <fo:inline font-weight="bold" >&#160;  <xsl:value-of select="organisme"/> </fo:inline> 
   Opérateur :&#160; <fo:inline font-weight="bold" >&#160;  <xsl:value-of select="operateur"/> </fo:inline> 
   </fo:block>
   <fo:block>Producteur : <xsl:value-of select="producteur_code"/>&#160; <xsl:value-of select="producteur_name"/></fo:block>
   <fo:block>Préleveur : <xsl:value-of select="preleveur_code"/>&#160; <xsl:value-of select="preleveur_name"/></fo:block>
   <fo:block>Déterminateur : <xsl:value-of select="determinateur_code"/>&#160; <xsl:value-of select="determinateur_name"/></fo:block>
   <fo:block>
  <xsl:choose>
  <xsl:when test="releve_dce = 1">Relevé réalisé dans le cadre de la DCE</xsl:when> 
  <xsl:otherwise>Relevé hors protocole DCE</xsl:otherwise>
  </xsl:choose>
  </fo:block>
  <fo:block>
  Protocole : &#160;<xsl:value-of select="protocole_libelle"/>
 </fo:block>
 <fo:block linefeed-treatment="preserve">Observations :&#160;<xsl:value-of select="observation"/></fo:block>
  
   <fo:block border="thin" space-before.optimum="1em"/>
  <fo:block font-weight="bold">Localisation géographique :</fo:block>

  <fo:block>
  Cours d'eau :&#160;<xsl:value-of select="cours_eau"/>
  <xsl:choose>
  <xsl:when test="typo_id > 0">
   &#160;Typologie nationale :&#160; <xsl:value-of select="typo_name"/>
  &#160;IBMR de référence :&#160; <xsl:value-of select="ibmr_ref"/> 
  </xsl:when>
  </xsl:choose>
  </fo:block>
  <fo:block>
  Coordonnées amont : X (L93) :&#160; <xsl:value-of select="coord_x"/>&#160;(wgs84 : 
  <xsl:value-of select="wgs84_x"/>)
  &#160; Y (L93) : &#160;<xsl:value-of select="coord_y"/>&#160;
  (wgs84 :&#160;<xsl:value-of select="wgs84_y"/>)
  </fo:block>
  <fo:block>Coordonnées aval : X (L93) :&#160; <xsl:value-of select="lambert_x_aval"/>&#160; (wgs84 : <xsl:value-of select="wgs84_x_aval"/>)
  Y (L93) : &#160;<xsl:value-of select="lambert_y_aval"/>&#160;
  (wgs84 :&#160;<xsl:value-of select="wgs84_y_aval"/>)
  </fo:block>
  <fo:block>Altitude : <xsl:value-of select="altitude"/>
   &#160;rive : &#160;<xsl:value-of select="rive_libelle"/>
  &#160;Longueur : &#160; <xsl:value-of select="longueur"/>
  &#160;Largeur : &#160; <xsl:value-of select="largeur"/>  
  </fo:block>
  <fo:block>
  Météo : <xsl:value-of select="meteo_libelle"/>
  &#160;Turbidité : <xsl:value-of select="turbidite_libelle"/>
  &#160;Hydrologie : <xsl:value-of select="hydrologie_libelle"/>
 </fo:block>
 
  <fo:block border="thin" space-before.optimum="1em"/>
  <fo:block font-weight="bold">Calcul IBMR :</fo:block>
  <fo:block>
  IBMR : <fo:inline font-weight="bold"> <xsl:value-of select="ibmr_value"/></fo:inline>
   <xsl:choose>
  <xsl:when test="typo_id > 0">
  &#160;Classe de qualité : <xsl:value-of select="classe_etat_libelle"/>
  </xsl:when>
  </xsl:choose>
  &#160;Niveau trophique : <xsl:value-of select="niveau_trophique_libelle"/>
  </fo:block>
  <fo:block>
  Robustesse : <xsl:value-of select="robustesse_value"/>
    <xsl:choose>
  <xsl:when test="typo_id > 0">
   &#160;Classe de qualité : <xsl:value-of select="robustesse_classe_etat_libelle"/>
   </xsl:when>
   </xsl:choose>
  &#160;Niveau trophique : <xsl:value-of select="rnt_libelle"/>
  </fo:block>
  <fo:block>
  &#160;Taxon associé : <xsl:value-of select="taxon_robustesse"/>
  &#160;Nb taxons avec même EK max : <xsl:value-of select="ek_nb_robustesse"/> 
  </fo:block>
  <xsl:choose>
  <xsl:when test="seee_ibmr > 0">
   <fo:block font-weight="bold">IBMR calculé par le SEEE :</fo:block>
   <fo:block>
   Calcul effectué le : <xsl:value-of select="seee_date"/>
   &#160;Version : <xsl:value-of select="seee_version"/>
   </fo:block>
    <fo:block>
  IBMR : <fo:inline font-weight="bold"> <xsl:value-of select="seee_ibmr"/></fo:inline>
  &#160;Robustesse : <xsl:value-of select="seee_robustesse_value"/>
  &#160;Taxon associé : <xsl:value-of select="seee_taxon_robustesse"/>
  </fo:block>
  </xsl:when>
  </xsl:choose>
  
  <fo:block keep-with-next.within-page="always">Coefficients :</fo:block>
  <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid"  keep-together.within-page="always">
  <fo:table-column column-width="30%"/>
  <fo:table-column column-width="10%" />
  <fo:table-column column-width="20%" />
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="20%" />
  <fo:table-column column-width="5%" />
 <fo:table-body  border-style="solid" >
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Cote spécifique (moy) :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="cs_moy"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>minimum :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="cs_min"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>maximum :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="cs_max"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Coef sténo (moy) :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="coef_moy"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>minimum :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="coef_min"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>maximum :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="coef_max"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
  </fo:table-body>
  </fo:table>
   <fo:block keep-with-next.within-page="always">Nombre de taxons :</fo:block>
  <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid"  keep-together.within-page="always">
  <fo:table-column column-width="25%"/>
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
 <fo:table-body  border-style="solid" >
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Hétérotrophes :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_het"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Algues :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_alg"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Bryophytes :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_bry"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Ptéridophytes :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_pte"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Phanérogames :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_pha"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Lichens :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_lic"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
  </fo:table-body>
  </fo:table>
  
   <fo:block keep-with-next.within-page="always">Totaux :</fo:block>
  <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid"  keep-together.within-page="always">
  <fo:table-column column-width="25%"/>
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
 <fo:table-body  border-style="solid" >
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Nbre total de taxons :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_total"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Nbre de taxons (contrib) :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_contrib"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block></fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Sténo 1 :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_steno1"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Sténo 2 :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_steno2"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Sténo 3 :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="nbtaxon_steno3"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
  </fo:table-body>
  </fo:table>
 
  
  <xsl:for-each select="unite_releve">
  <fo:block>
  <fo:block border="thin" space-before.optimum="1em"/>
  <fo:inline font-weight="bold">Description de l'unité de relevé : <xsl:value-of select="type_ur_libelle"/>
  &#160;(<xsl:value-of select="pc_ur"/>%)</fo:inline>
  </fo:block>
  <fo:block>Longueur :&#160;<xsl:value-of select="longueur_ur"/>
  &#160;Largeur :&#160;<xsl:value-of select="largeur_ur"/>
  Faciès dominant : &#160;<xsl:value-of select="facies_libelle"/>
  </fo:block>
  <fo:block font-style="italic" space-before.optimum="1em">Échelle : 0 : absent, 1 : &lt; 1%, 2 : &lt; 10%, 3 : &lt; 25%, 4 : &lt; 75%, 5 : &gt;=75%</fo:block>
  <fo:block border="thin" space-before.optimum="1em" keep-with-next.within-page="always">Type de faciès :</fo:block>
   <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid" keep-together.within-page="always" >
  <fo:table-column column-width="25%"/>
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
 <fo:table-body  border-style="solid" >
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Chenal lentique :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="ch_lentique"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Plat lentique :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pl_lentique"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Mouille :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="mouille"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
  	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Fosse de dissipation :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="fosse_dissipation"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Chenal lotique :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="ch_lotique"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Radier :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="radier"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
  	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Cascade :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="waterfall"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Plat courant :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pl_courant"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Rapide :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="rapide"/></fo:block>
  		</fo:table-cell>
   	</fo:table-row>
  	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Autre type : </fo:block>
  		</fo:table-cell>
  		<fo:table-cell><fo:block><xsl:value-of select="autretypeclass"/></fo:block></fo:table-cell>
  		<fo:table-cell>
  		<fo:block><xsl:value-of select="facies_autre_type_libelle"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block></fo:block>
  		</fo:table-cell>  		
  	</fo:table-row>
 </fo:table-body>
 </fo:table>
 
 <fo:block border="thin" space-before.optimum="1em" keep-with-next.within-page="always">Profondeur :</fo:block>
   <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid"  keep-together.within-page="always">
  <fo:table-column column-width="15%"/>
  <fo:table-column column-width="3%" />
  <fo:table-column column-width="15%" />
  <fo:table-column column-width="3%" />
  <fo:table-column column-width="15%" />
  <fo:table-column column-width="3%" />
 <fo:table-column column-width="15%" />
  <fo:table-column column-width="3%" />
 <fo:table-column column-width="15%" />
  <fo:table-column column-width="3%" />
<fo:table-body  border-style="solid" >
 	<fo:table-row>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&lt; 10cm :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="p1"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&lt; 50cm :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="p2"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&lt; 1m :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="p3"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&lt; 2m :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="p4"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&gt;= 2m :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="p5"/></fo:block>
  		</fo:table-cell>
   	</fo:table-row>
   </fo:table-body>
 </fo:table>
<fo:block border="thin" space-before.optimum="1em" keep-with-next.within-page="always">Vitesse du courant :</fo:block>
   <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid"  keep-together.within-page="always">
  <fo:table-column column-width="15%"/>
  <fo:table-column column-width="3%" />
  <fo:table-column column-width="15%" />
  <fo:table-column column-width="3%" />
  <fo:table-column column-width="15%" />
  <fo:table-column column-width="3%" />
 <fo:table-column column-width="15%" />
  <fo:table-column column-width="3%" />
 <fo:table-column column-width="15%" />
  <fo:table-column column-width="3%" />
 <fo:table-body  border-style="solid" >
 	<fo:table-row>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&lt; 0,05 m/s :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="v1"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&lt; 0,2 m/s :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="v2"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&lt; 0,5 m/s :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="v3"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&lt; 1 m/s :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="v4"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell text-align="right"> 
  			<fo:block>&gt;= 1 m/s :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="v5"/></fo:block>
  		</fo:table-cell>
   	</fo:table-row>
   </fo:table-body>
 </fo:table>

 
 <fo:block border="thin" space-before.optimum="1em" keep-with-next.within-page="always">Éclairement :</fo:block>
   <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid"  keep-together.within-page="always">
  <fo:table-column column-width="25%"/>
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
 <fo:table-body  border-style="solid" >
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Très ombragé :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="tres_ombrage"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Ombragé :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="ombrage"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Peu ombragé :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="peu_ombrage"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Éclairé :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="eclaire"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Très éclairé :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="tres_eclaire"/></fo:block>
  		</fo:table-cell>
   	</fo:table-row>
   </fo:table-body>
 </fo:table>
 
 <fo:block border="thin" space-before.optimum="1em" keep-with-next.within-page="always">Type de substrat :</fo:block>
   <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid"  keep-together.within-page="always">
  <fo:table-column column-width="25%"/>
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
 <fo:table-body  border-style="solid" >
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Vase, limons :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="vase_limons"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Terre, argile, marne, tourbe :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="terre_marne_tourbe"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Cailloux, pierres, galets :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="cailloux_pierres"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>Blocs, dalles :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="blocs_dalles"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Sables, graviers :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="sable_graviers"/></fo:block>
  		</fo:table-cell>
 		<fo:table-cell> 
  			<fo:block>Racines, branchages :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="racines"/></fo:block>
  		</fo:table-cell>
   	</fo:table-row>
	<fo:table-row>
   		<fo:table-cell> 
  			<fo:block>Débris organiques :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="debris_org"/></fo:block>
  		</fo:table-cell>
   		<fo:table-cell> 
  			<fo:block>Artificiels :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="artificiel"/></fo:block>
  		</fo:table-cell>
  	</fo:table-row>
   </fo:table-body>
 </fo:table>
 
 <fo:block border="thin" space-before.optimum="1em" keep-with-next.within-page="always">Végétalisation :</fo:block>
   <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid" keep-together.within-page="always">
  <fo:table-column column-width="25%"/>
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
  <fo:table-column column-width="25%" />
  <fo:table-column column-width="5%" />
  <fo:table-body  border-style="solid" >
 	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>% surface végétalisée :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pc_vegetalisation"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>Périphyton :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell><fo:block></fo:block></fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="periphyton_libelle"/></fo:block>
  		</fo:table-cell>
    	</fo:table-row>
	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>% végétation flottante :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pc_flottante"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>% végétation immergée :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pc_immerg"/></fo:block>
  		</fo:table-cell>
    	</fo:table-row>
	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>% hélophytes :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pc_helophyte"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>% hétérotrophes :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pc_heterot"/></fo:block>
  		</fo:table-cell>
 		<fo:table-cell> 
  			<fo:block>% algues :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pc_algues"/></fo:block>
  		</fo:table-cell>
   	</fo:table-row>
	<fo:table-row>
  		<fo:table-cell> 
  			<fo:block>% bryophytes :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pc_bryo"/></fo:block>
  		</fo:table-cell>
  		<fo:table-cell> 
  			<fo:block>% ptéridophytes et lichens :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pc_lichen"/></fo:block>
  		</fo:table-cell>
 		<fo:table-cell> 
  			<fo:block>% phanérogames :</fo:block>
  		</fo:table-cell>
  		<fo:table-cell>
  			<fo:block><xsl:value-of select="pc_phanero"/></fo:block>
  		</fo:table-cell>
   	</fo:table-row>

  
  </fo:table-body>
 </fo:table>
  </xsl:for-each>
 
  <fo:block font-weight="bold" border="thin" space-before.optimum="1em" keep-with-next.within-page="always">Taxons</fo:block>
    <fo:table table-layout="fixed" border-collapse="collapse" width="100%" border-style="solid"  keep-together.within-page="always">
  	<fo:table-column column-width="20%"/>
  	<fo:table-column column-width="10%"/>
  	<fo:table-column column-width="10%"/>
  	<fo:table-column column-width="10%"/>
  	<fo:table-column column-width="10%"/>
  	<fo:table-column column-width="5%"/>
  	<fo:table-column column-width="5%"/>
  	<fo:table-column column-width="10%"/>
  	<fo:table-column column-width="10%"/>
  	<fo:table-column column-width="10%"/>
  
 	<fo:table-header border-style="solid" >
  		<fo:table-cell><fo:block  text-align="center" border-left-style="solid" >Taxon</fo:block></fo:table-cell>
  		<fo:table-cell><fo:block text-align="center"  border-left-style="solid"  linefeed-treatment="preserve">cd&#160;&#xA;taxon&#160;&#160;&#160;</fo:block></fo:table-cell>
  		<fo:table-cell><fo:block text-align="center"  border-left-style="solid" linefeed-treatment="preserve">&#160;cd &#160;&#160;&#xA;valid&#160;&#160;</fo:block></fo:table-cell>
  		<fo:table-cell><fo:block text-align="center"  border-left-style="solid"  border-right-style="solid" linefeed-treatment="preserve">&#160;cd &#160;&#160;&#xA;contrib&#160;</fo:block></fo:table-cell>
  		<fo:table-cell><fo:block text-align="center"  border-left-style="solid"  border-right-style="solid" linefeed-treatment="preserve">&#160;&#xA;&#160;&#160;&#160;&#160;Sandre&#160;&#160;</fo:block></fo:table-cell>
  		<fo:table-cell><fo:block text-align="center"  border-left-style="solid" linefeed-treatment="preserve">&#160;&#xA;&#160;&#160;cf&#160;&#160;&#160;&#160;</fo:block></fo:table-cell>
   		<fo:table-cell><fo:block text-align="center"  border-left-style="solid" border-right-style="solid" linefeed-treatment="preserve">Cote&#xA;&#160;spe&#160;&#160;</fo:block></fo:table-cell>
  		<fo:table-cell><fo:block text-align="center"  border-left-style="solid" linefeed-treatment="preserve">Coef steno</fo:block></fo:table-cell>
  		<fo:table-cell><fo:block text-align="center"  border-left-style="solid"   linefeed-treatment="preserve">&#160;UR1&#160;&#160;&#160;&#xA;%&#160;&#160;</fo:block></fo:table-cell>
  		<fo:table-cell><fo:block text-align="center"  border-left-style="solid" border-right-style="solid" linefeed-treatment="preserve">&#160;UR2&#160;&#160;&#160;&#xA;%&#160;&#160;</fo:block></fo:table-cell>
  	</fo:table-header>
 	<fo:table-body >
 	<xsl:for-each select="taxon">
  		<fo:table-row>
  		<fo:table-cell border-left-style="solid" > 
  			<fo:block font-size="small">
  			<xsl:value-of select="nom_taxon"/>
  			</fo:block>
  		</fo:table-cell>
   		<fo:table-cell border-left-style="solid" > 
  			<fo:block>
  			<xsl:value-of select="cd_taxon"/>
  			</fo:block>
  		</fo:table-cell>
  		<fo:table-cell border-left-style="solid" > 
  			<fo:block>
  			<xsl:value-of select="cd_valide"/>
  			</fo:block>
  		</fo:table-cell>
  		<fo:table-cell border-left-style="solid" > 
  			<fo:block>
  			<xsl:value-of select="cd_contrib"/>
  			</fo:block>
  		</fo:table-cell>
  		<fo:table-cell border-left-style="solid" > 
  			<fo:block text-align="center">
  			<xsl:value-of select="cd_sandre"/>
  			</fo:block>
  		</fo:table-cell>
  		<fo:table-cell border-left-style="solid" > 
  			<fo:block text-align="center">
  			<xsl:value-of select="cf"/>
  			</fo:block>
  		</fo:table-cell>
  		<fo:table-cell border-left-style="solid" > 
  			<fo:block text-align="center">
  			<xsl:value-of select="cote_spe"/>
  			</fo:block>
  		</fo:table-cell>
  		<fo:table-cell border-left-style="solid" > 
  			<fo:block text-align="center">
  			<xsl:value-of select="coef_steno"/>
  			</fo:block>
  		</fo:table-cell>
  		<fo:table-cell border-left-style="solid" > 
  			<fo:block text-align="center">
  			<xsl:value-of select="pc_ur1"/>
  			</fo:block>
  		</fo:table-cell>
  		<fo:table-cell border-left-style="solid" border-right-style="solid" > 
  			<fo:block text-align="center">
  			<xsl:value-of select="pc_ur2"/>
  			</fo:block>
  		</fo:table-cell>
   		</fo:table-row>
  	</xsl:for-each>
  	</fo:table-body>
  </fo:table>
  
  <fo:block border="thin" space-before.optimum="1em"/>
 
  <fo:block page-break-after="always"/>

  </xsl:template>
<fo:static-content flow-name="xsl-region-after">
<fo:block font-size="9pt" text-align="center">
 - <fo:page-number/> -
</fo:block>
</fo:static-content>

</xsl:stylesheet>

