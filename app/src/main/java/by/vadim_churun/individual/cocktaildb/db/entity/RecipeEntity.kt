package by.vadim_churun.individual.cocktaildb.db.entity

class RecipeEntity(
    val drink: DrinkEntity,
    val ingredients: List<IngredientInRecipeEntity>
)