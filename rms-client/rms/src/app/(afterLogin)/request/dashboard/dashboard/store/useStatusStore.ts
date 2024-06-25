import {create, SetState} from "zustand";
import {Status} from "@/model/Status";

interface StatusStore {
    status: Status;
    setStatus: (newStatus: Status) => void;
}

const useStatusStore = create<StatusStore>((set: SetState<StatusStore>) => ({
    status: Status.TOTAL,
    setStatus: (newStatus) => set({ status: newStatus})
}));

export const useStatus = () => useStatusStore((state) => state.status);
export const useSetStatus = () => useStatusStore((state) => state.setStatus);