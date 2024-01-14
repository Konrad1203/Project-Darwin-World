package model.animal;

public interface GenomeFactory {

    Genome createGenome();

    Genome createGenome(int[] genomeList);
}
