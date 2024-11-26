import { create, SetState } from 'zustand';

interface ProbandState {
    proband: string;
    setProband: (newProband: string) => void;
    relationship: string;
    setRelationship: (newRelationship: string) => void;
    probandModalOpen: boolean;
    setProbandModalOpen: (newProbandModal: boolean) => void;
}

const useProbandStore = create<ProbandState>((set: SetState<ProbandState>) => ({
    proband: '',
    setProband: (newProband: string) => set({ proband: newProband }),
    relationship: '',
    setRelationship: (newRelationship: string) => set({ relationship: newRelationship }),
    probandModalOpen: false,
    setProbandModalOpen: (newProbandModal: boolean) => set({ probandModalOpen: newProbandModal })
}));

export const useProband = () => useProbandStore((state) => state.proband);
export const useSetProband = () => useProbandStore((state) => state.setProband);

export const useRelationship = () => useProbandStore((state) => state.relationship);
export const useSetRelationship = () => useProbandStore((state) => state.setRelationship);

export const useProbandModalOpen = () => useProbandStore((state) => state.probandModalOpen);
export const useSetProbandModalOpen = () => useProbandStore((state) => state.setProbandModalOpen);