import ru.ifmo.se.pokemon.*;
import Pokemons.*;

public static void main(String[] args) {

    Aerodactyl aerodactyl = new Aerodactyl("MishLetuchiy", 1);

    Joltik joltik = new Joltik("PavukMini", 1);
    Galvantula galvantula = new Galvantula("PavukBig", 1);

    Tynamo tynamo = new Tynamo("ChervPlosky", 1);
    Eelektrik eelektrik = new Eelektrik("ChervTolsty", 1);
    Eelektross eelektross = new Eelektross("ChervDliny", 1);

    Battle battle = new Battle();

    battle.addAlly(aerodactyl);
    battle.addAlly(joltik);
    battle.addAlly(galvantula);

    battle.addFoe(tynamo);
    battle.addFoe(eelektrik);
    battle.addFoe(eelektross);

    battle.go();
}