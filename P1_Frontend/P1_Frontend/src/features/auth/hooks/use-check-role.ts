import {protectedInstance, addInterceptors} from "@/lib/axios-config";
import { useQuery } from "@tanstack/react-query";


export function useCheckRole(): any {

  return useQuery({
    queryKey: ["role"],
    queryFn: async () => {
      try {
        //addInterceptors(protectedInstance);
        const resp = await protectedInstance.get("/role", {headers: {'Authorization': localStorage.getItem("token")}});//check where to go instead
        return resp.data.role as string;
      } catch (e) {
        console.error(e);
        return null; // consider redirecting
      }
    },
    refetchOnWindowFocus: false,
    refetchOnReconnect: false,
  });
}
