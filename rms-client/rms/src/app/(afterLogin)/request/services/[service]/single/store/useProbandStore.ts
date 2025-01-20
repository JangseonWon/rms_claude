import { create, SetState } from 'zustand';
import {Request} from '@/model/Request'

interface ProbandState {
    proband: string;
    setProband: (newProband: string) => void;
    probandRequest: Request | null;
    setProbandRequest: (probandRequest: Request) => void;
    relationship: string;
    setRelationship: (newRelationship: string) => void;
    probandModalOpen: boolean;
    setProbandModalOpen: (newProbandModal: boolean) => void;
}

const useProbandStore = create<ProbandState>((set: SetState<ProbandState>) => ({
    proband: '',
    setProband: (newProband: string) => set({ proband: newProband }),
    probandRequest: null,
    setProbandRequest: (probandRequest: Request) => set({probandRequest}),
    relationship: '',
    setRelationship: (newRelationship: string) => set({ relationship: newRelationship }),
    probandModalOpen: false,
    setProbandModalOpen: (newProbandModal: boolean) => set({ probandModalOpen: newProbandModal })
}));

export const useProband = () => useProbandStore((state) => state.proband);
export const useSetProband = () => useProbandStore((state) => state.setProband);
export const useProbandRequest = () => useProbandStore((state) => state.probandRequest);
export const useSetProbandReqeust = () => useProbandStore((state) => state.setProbandRequest)

export const useRelationship = () => useProbandStore((state) => state.relationship);
export const useSetRelationship = () => useProbandStore((state) => state.setRelationship);

export const useProbandModalOpen = () => useProbandStore((state) => state.probandModalOpen);
export const useSetProbandModalOpen = () => useProbandStore((state) => state.setProbandModalOpen);