import {create, SetState} from 'zustand';
import {Categories} from "@/model/Categories";

interface CategoryState {
    category: Categories[];
    setCategory: (newService: Categories[]) => void;
    selectCategory: Categories | null;
    setSelectCategory: (newService: Categories) => void;
}

const useCategoryStore = create<CategoryState>((set: SetState<CategoryState>) => ({
    category: [],
    setCategory: (newCategory) => set({ category: newCategory }),
    selectCategory: null,
    setSelectCategory: (newCategory) => set({ selectCategory: newCategory })
}));

export const useCategory = () => useCategoryStore((state) => state.category);
export const useSetCategory = () => useCategoryStore((state) => state.setCategory);
export const useSelectCategory = () => useCategoryStore((state) => state.selectCategory);
export const useSetSelectCategory = () => useCategoryStore((state) => state.setSelectCategory);
