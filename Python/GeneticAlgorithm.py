import pygad

from Chromosome import PracticalChromosome
from Fitness import practicalFitness

nGenerations = 1
nParentsMating = 10
nSolPerPop = 30

practicalChromosome = PracticalChromosome()
practicalChromosome.rooms.append(0)

GeneticAlgorithm = pygad.GA(num_generations=nGenerations,
                            num_parents_mating=nParentsMating,
                            sol_per_pop=nSolPerPop,
                            num_genes=practicalChromosome.getChromosomeLength(),
                            gene_space=practicalChromosome.rooms,
                            parent_selection_type="rank",
                            crossover_type="uniform",
                            mutation_type="adaptive",
                            mutation_num_genes=(30, 5),
                            stop_criteria="reach_1",
                            fitness_func=practicalFitness,
                            parallel_processing=nSolPerPop,
                            gene_type=int,
                            keep_elitism=0,
                            keep_parents=0,
                            save_best_solutions=True,
                            initial_population=[[0 for _ in range(practicalChromosome.getChromosomeLength())] for _ in range(nSolPerPop)]
                            )

GeneticAlgorithm.run()


# Returning the details of the best solution.
solution, solution_fitness, solution_idx = GeneticAlgorithm.best_solution()
practicalChromosome.setChromosome(solution)
practicalChromosome.insertTimeTable()