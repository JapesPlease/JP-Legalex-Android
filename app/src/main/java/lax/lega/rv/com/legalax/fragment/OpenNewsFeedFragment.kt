                      package lax.lega.rv.com.legalax.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.open_newsfeed_activity.view.*
import lax.lega.rv.com.legalax.R
import lax.lega.rv.com.legalax.common.Utils
import lax.lega.rv.com.legalax.pojos.GetNewsFeedPojo
import lax.lega.rv.com.legalax.retrofit.Constant
import java.util.*


class OpenNewsFeedFragment : Fragment()  {
    lateinit var v: View
    private var pos: Int? = null
    var id: Int? = null
    private lateinit var player: SimpleExoPlayer


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        v = inflater.inflate(R.layout.open_newsfeed_activity, container, false)
        listeners()
        setData()
        return v
    }
    private fun listeners(){
        v.ivPlay.setOnClickListener {
          //  v.videoView.start()
            v.ivThumbnail.visibility=View.GONE
            v.ivPlay.visibility=View.GONE
         //   setData()
        }


    }
    private fun setData() {
        val transactionList = arguments!!.getSerializable("list") as ArrayList<GetNewsFeedPojo.Response>
        pos = arguments!!.getInt("size")
        id= transactionList[pos!!].id
        v.txt_title.text = transactionList[pos!!].title.toString()
        v.txt_story.text = transactionList[pos!!].story.toString()
        v.txt_authorname.text = transactionList[pos!!].authorName.toString()
        v.txt_date.text = transactionList[pos!!].date.toString()
        v.txt_desc.text = transactionList[pos!!].description.toString()

        if (transactionList[pos!!].image == null || transactionList[pos!!].image == "") {
            v.img_post.visibility=View.GONE
            v.flVideo.visibility=View.VISIBLE
            Picasso.with(context).load(Utils.SERVER_URL + transactionList[pos!!].video_thumb).into(v.ivThumbnail)
            setUpVideo(Utils.BASE_URL_VIDEO + transactionList[pos!!].video)
//            image=Constant.NEWSFEEDURL + transactionList.get(pos!!).image
//            file1=File(image)
        }else{
            Picasso.with(context).load(Constant.NEWSFEEDURL + transactionList[pos!!].image).into(v.img_post)
            v.img_post.visibility=View.VISIBLE
            v.flVideo.visibility=View.GONE

        }

        arguments?.remove("list")

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun setUpVideo(videoUrl: String) {
        val uri: Uri = Uri.parse(videoUrl)
      //  v.videoView.setSource(videoUrl)
       // v.videoView.start()


        player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(requireContext()),
                DefaultTrackSelector(),
                DefaultLoadControl()
        )

        v.videoView.player = player
        v.videoView.setShowBuffering(true)

        player.seekTo(0, 0)

        val mediaSource = buildMediaSource(uri)
        player.prepare(mediaSource, true, false)
        player.playWhenReady = true
        player.addListener(ComponentListener())
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri)
    }

    class ComponentListener : Player.DefaultEventListener() {
        override fun onPlayerStateChanged(
                playWhenReady: Boolean,
                playbackState: Int
        ) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED -"
                else -> "UNKNOWN_STATE -"
            }
            Log.e("Expo Player", "changed state to " + stateString
                    + " playWhenReady: " + playWhenReady
            )
        }
    }
}
