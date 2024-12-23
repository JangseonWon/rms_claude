import create from 'zustand';
import { Request } from '@/model/Request';

interface RequestState {
    request: Request | null;
    setRequest: (request: Request | ((prevState: Request | null) => Request)) => void;
    resetRequest: () => void;
}

export const useRequestStore = create<RequestState>((set) => ({
    request: null,
    setRequest: (request) =>
        set((state) => ({
            request: typeof request === 'function' ? request(state.request) : request,
        })),
    resetRequest: () => set(() => ({ request: null })),
}));