package com.example.tfgproject.ui.teamDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.tfgproject.R
import com.example.tfgproject.databinding.PlayerItemBinding
import com.example.tfgproject.model.Player

class PlayerAdapter(private var players: List<Player>) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(private val binding: PlayerItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun getPositionImageUrl(position: Int): String {
            return when (position) {
                //pitcher
                1 -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/noun-baseball-pitcher-2745168.png?alt=media&token=5817cc45-2618-4384-bd11-00e61b678775"
                2 -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/catcher%20logo.png?alt=media&token=bd536903-378b-46e9-8e91-b2a784a93f06"
                3 -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/noun-mitt-1941039.png?alt=media&token=587d6ce6-3d13-4440-b2a4-da79454a87bd"
                4 -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/noun-home-base-5005428.png?alt=media&token=4f387627-60f7-42d6-868d-778a81145809"
                5 -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/noun-home-base-5005447.png?alt=media&token=3f4eb468-46ef-408b-9a6c-2e62b27f79e7"
                6 -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/shortstop.png?alt=media&token=f05da330-2c20-461b-83bf-b5d1783201c2"
                7 -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/noun-baseball-player-54549.png?alt=media&token=c39eff19-4e52-4379-857d-ba2f2549812e"
                8 -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/noun-catcher-643797.png?alt=media&token=485f3826-d24a-479a-94e1-97ed7c211bdd"
                9 -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/rightfield.png?alt=media&token=68854fa0-b8ef-439f-983d-486238a92c6c"

                else -> "https://firebasestorage.googleapis.com/v0/b/home-slam.appspot.com/o/unknown.png?alt=media&token=756b782e-9cdc-40ad-ae01-13ae9677ce28"
            }
        }

        fun bind(player: Player) {
            binding.textViewPlayerName.text = player.name
            binding.imageViewPlayer.load(player.imageUrl) {
                transformations(RoundedCornersTransformation(10f))
                placeholder(R.drawable.placeholder_image)
                error(R.drawable.error_image)
            }

            val positions = player.positions
            if (positions != null) {
                if (positions.isNotEmpty()) {
                    if (positions != null) {
                        binding.imageViewIcon1.load(getPositionImageUrl(positions.getOrElse(0) { -1 })) {
                            transformations(RoundedCornersTransformation(20f))
                            placeholder(R.drawable.placeholder_image)
                            error(R.drawable.error_image)
                        }

                    }
                }
            }
            if (positions != null) {
                if (positions.size > 1) {
                    if (positions != null) {
                        binding.imageViewIcon2.load(getPositionImageUrl(positions.getOrElse(1) { -1 })) {
                            transformations(RoundedCornersTransformation(20f))
                            placeholder(R.drawable.placeholder_image)
                            error(R.drawable.error_image)
                        }
                    }
                }
            }
            if (positions != null) {
                if (positions.size > 2) {
                    if (positions != null) {
                        binding.imageViewIcon3.load(getPositionImageUrl(positions.getOrElse(2) { -1 })) {
                            transformations(RoundedCornersTransformation(20f))
                            placeholder(R.drawable.placeholder_image)
                            error(R.drawable.error_image)
                        }
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding = PlayerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount() = players.size

    fun updatePlayers(newPlayers: List<Player>) {
        players = newPlayers
        notifyDataSetChanged()
    }


}