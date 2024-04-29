import {create} from "zustand";


interface RequestState {
    mode: 'new' | 'comment',
    setMode(mode: 'new' | 'comment'): void;
    reset(): void;
}

export const useRequestStore = create<RequestState>((set) => ({
    mode: 'new',
    setMode(mode) {
        set({ mode });
    },
    reset() {
        set({
            mode: 'new',
        })
    }
}));