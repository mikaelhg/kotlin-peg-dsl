package io.mikael.karslet

object PxTestData {

    val rows = mapOf(
        """FOBBO""" to false,
        """FOO=3BAR;""" to true,
        """LANGUAGES="fi","sv","en";""" to true,
        """DECIMALS=0;""" to true,
        """SUBJECT-AREA[sv]="Besiktningar av personbilar";""" to true,
        """VALUES[sv]("Besiktningsår")="2017","2018","2019","2020";""" to true,
        """VALUENOTE[en]("Information","Average mileage")="Average mileage";""" to true,
        "NOTE=\"<A HREF='https://www.stat.fi/til/vtp/rev.html' TARGET=_blank>Tietojen tarkentuminen</A>#<A HREF='https://www.stat.fi/til/vtp/meta.html' TARGET=_blank>Tilaston kuvaus</A>#<A HREF='https://www.stat.fi/til/vtp/laa.html' TARGET=_blank>Laatuselosteet</A>#\"\n" +
                "\"<A HREF='https://www.stat.fi/til/vtp/men.html' TARGET=_blank>Menetelmäseloste</A>#<A HREF='https://www.stat.fi/til/vtp/kas.html' TARGET=_blank>Käsitteet ja määritelmät</A>#<A HREF='https://www.stat.fi/til/vtp/uut.html' TARGET=_blank>Muutoksia tässä \"\n" +
                "\"tilastossa</A>##... tieto on salassapitosäännön alainen#... tieto on salassapitosäännön alainen##Poiketen muista kansantalouden tilinpidon taulukoista, tässä on esitetty (pl. P51K Kiinteän pääoman bruttomuodostus)  volyymisarjat sekä perusvuoden 2010 \"\n" +
                "\"hintaisina (kantaindeksi; voidaan summata alasarjoista) että viitevuoden 2010 sarjoina (ketjuindeksi; ei voida summata alasarjoista). Viitevuoden 2010 volyymisarja on muodostettu ketjuttamalla edellisen vuoden hintaiset sarjat.\";"
        to true,
    )

}
