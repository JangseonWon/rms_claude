import {create, SetState} from 'zustand';

interface UserId {
    id: string
}
interface UserState {
    id: string;
    setId: (newUserId: string) => void;
}

const useUserStore = create<UserState>((set: SetState<UserState>) => ({
    id: "",
    setId: (newUser) => set({ id: newUser })
}));

export const useLoginUser = () => useUserStore((state) => state.id);
export const useSetLoginUser = () => useUserStore((state) => state.setId);