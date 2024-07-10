import {create, SetState} from 'zustand';
import {Categories} from "@/model/Categories";

interface CategoryState {
    category: Categories[];
    setCategory: (newService: Categories[]) => void;
}

const useCategoryStore = create<CategoryState>((set: SetState<CategoryState>) => ({
    category: [],
    setCategory: (newCategory) => set({ category: newCategory })
}));

export const useCategory = () => useCategoryStore((state) => state.category);
export const useSetCategory = () => useCategoryStore((state) => state.setCategory);