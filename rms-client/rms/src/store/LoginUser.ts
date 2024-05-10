import {create, SetState} from 'zustand';
import {DefaultSession} from "next-auth";

interface User {
    id: string,
    branch_name: string,
    branch_serial: string,
    email: string,
    name: string,
    role: string,
    state: string,
    type: string
}

interface UserState {
    user: User | null;
    setUser: (newUser: ({
        id: string;
        branch_name: string;
        branch_serial: string;
        email: string;
        name: string;
        role: string;
        state: string;
        type: string
    } & DefaultSession["user"]) | undefined) => void;
}

const useUserStore = create<UserState>((set: SetState<UserState>) => ({
    user: null,
    setUser: (newUser) => set({ user: newUser })
}));

export const useLoginUser = () => useUserStore((state) => state.user);
export const useSetLoginUser = () => useUserStore((state) => state.setUser);