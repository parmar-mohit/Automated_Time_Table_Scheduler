from Chromosome import *

def practicalFitness(chromosome,solution_index):
    # For Fitness, we will count the number of conflicts in given timetable
    practicalChromosome = PracticalChromosome()
    practicalChromosome.setChromosome(chromosome)
    return 1.0 / ( practicalChromosome.getConflictCount() + 0.01 )

def lectureFitness(chromosome,solution_idx):
    # For Fitness, we will count the number of conflicts in given timetable
    lectureChromosome = LectureChromosome()
    lectureChromosome.setChromosome(chromosome)
    return 1.0 / ( lectureChromosome.getConflictCount() + 0.01 )
