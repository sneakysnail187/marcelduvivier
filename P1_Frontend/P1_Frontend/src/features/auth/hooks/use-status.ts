import { useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance } from "@/lib/axios-config";
import { toast } from "sonner";

interface StatusSchema {
    reimbId: number;
    status: string;
}

export function useStatus() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async ({reimbId, status}: StatusSchema) => {
            console.log(reimbId, status); 
            const resp = await axiosInstance.patch(`/reimbursement/status/${reimbId}`,{status}, {headers: {'Authorization': localStorage.getItem("token")}});
            return resp.data;
        },
        
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: ["ticket-status"], //check this later
            });
            toast.success("Status updated successfully.");
        },
        onError: () => {
            toast.error("Failed to update status.");
        },
        
    });
}